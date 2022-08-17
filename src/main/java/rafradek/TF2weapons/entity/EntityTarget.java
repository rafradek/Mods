package rafradek.TF2weapons.entity;

import java.text.DecimalFormat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class EntityTarget extends EntityArmorStand {

	public EntityTarget(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
	}
	
	public EntityTarget(World worldIn, double d, double d1, double e, boolean creative) {
		super(worldIn, d, d1, e);
		this.creative = creative;
	}

	private static final DataParameter<Float> LAST_DAMAGE = EntityDataManager.<Float>createKey(EntityTarget.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> DPS = EntityDataManager.<Float>createKey(EntityTarget.class, DataSerializers.FLOAT);
	
	public float[] dps = new float[3];
	
	public int startAttack = -1;
	public int lastAttack;
	public int deltaTime;
	public float total;
	
	public boolean creative;
	
	public void onUpdate() {
		super.onUpdate();
		if(!this.world.isRemote && this.ticksExisted % 5 == 0) {
			DecimalFormat format = new DecimalFormat("#.##");
			if (this.startAttack != -1 && this.ticksExisted - this.lastAttack >= 35) {
				this.startAttack = -1;
				this.total = 0;
			}
			else if(this.startAttack != 1) {
				
			}
			/*this.dps[2]=this.dps[1];
			this.dps[1]=this.dps[0];
			this.dps[0]=0f;
			DecimalFormat format = new DecimalFormat("#.##");*/
			this.setCustomNameTag("Last: "+format.format(this.getLastDamage())+ " DPS: "+format.format(total * (20f/(this.lastAttack - this.startAttack + this.deltaTime)))+" Total: "+format.format(total));
		}
	}
	
	public void entityInit() {
		super.entityInit();
        this.dataManager.register(LAST_DAMAGE, 0f);
        this.dataManager.register(DPS, 0f);
	}
	
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
	}
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!this.world.isRemote && !this.isDead)
        {
            if (DamageSource.OUT_OF_WORLD.equals(source) || (source.getTrueSource() instanceof EntityLivingBase && source.getDamageType().equals("player") && source.getTrueSource().isSneaking()))
            {
                this.setDead();
                return false;
            }
            else
            {
            	if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
                {
                    if (amount <= this.lastDamage)
                    {
                        return false;
                    }

                    amount -= this.lastDamage;
                    this.lastDamage = amount;
                }
                else
                {
                    this.lastDamage = amount;
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.maxHurtTime = 10;
                    this.hurtTime = this.maxHurtTime;
                }
            	
            	amount = ForgeHooks.onLivingDamage(this, source, this.applyPotionDamageCalculations(source, ArmorProperties.applyArmor(this, (NonNullList<ItemStack>) this.getArmorInventoryList(), source, ForgeHooks.onLivingHurt(this, source, amount))));
            	this.dataManager.set(LAST_DAMAGE, amount);
            	this.dps[0] += amount;
            	if(this.startAttack == -1) {
            		this.startAttack = this.ticksExisted;
            		this.total = 0;
            	}
            	this.deltaTime = this.ticksExisted - this.lastAttack;
            	this.lastAttack = this.ticksExisted;
            	
            	this.total += amount;
            	this.hurtResistantTime = 10;
            	DecimalFormat format = new DecimalFormat("#.##");
            	this.setCustomNameTag("Last: "+format.format(this.getLastDamage())+ " DPS: "+format.format(total * (20f/(this.lastAttack - this.startAttack  + this.deltaTime)))+" Total: "+format.format(total));
                for (ItemStack stack : this.getArmorInventoryList()) {
                	stack.setItemDamage(0);
                }
            	return this.creative;
            }
        }
        else
        {
            return false;
        }
    }
	public boolean getAlwaysRenderNameTag() {
		return true;
	}
	public float getLastDamage() {
		return this.dataManager.get(LAST_DAMAGE);
	}
	
	public float getDPS() {
		return this.dataManager.get(DPS);
	}
}
