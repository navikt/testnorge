import * as _ from 'lodash-es'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatStringDates, showLabel } from '@/utils/DataFormatter'
import { AvslagEllerBortfall, AvslagEllerBortfallVisning } from './AvslagEllerBortfallVisning'

type Opphold = {
	oppholdsstatus: {
		oppholdSammeVilkaar: object
		ikkeOppholdstilatelseIkkeVilkaarIkkeVisum: {
			avslagEllerBortfall: AvslagEllerBortfall
		}
		uavklart: boolean
	}
	oppholdstillatelse: boolean
}

export const Oppholdsstatus = ({ oppholdsstatus, oppholdstillatelse }: Opphold) => {
	const opphold = _.omitBy(oppholdsstatus, _.isNil)
	if (_.isEmpty(opphold) && !oppholdstillatelse) {
		return null
	}

	const oppholdsrettTyper = [
		'eosEllerEFTABeslutningOmOppholdsrett',
		'eosEllerEFTAVedtakOmVarigOppholdsrett',
		'eosEllerEFTAOppholdstillatelse',
	]
	// @ts-ignore
	const currentOppholdsrettType = oppholdsrettTyper.find((type) => opphold[type])

	const currentTredjelandsborgereStatus = () => {
		if (opphold.oppholdSammeVilkaar) {
			return 'Oppholdstillatelse eller opphold på samme vilkår'
		} else if (opphold.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum) {
			return 'Ikke oppholdstillatelse eller ikke opphold på samme vilkår'
		} else if (opphold.uavklart) {
			return 'Uavklart'
		}
		return null
	}

	const oppholdsrett = Boolean(currentOppholdsrettType)
	const tredjelandsborger = Boolean(currentTredjelandsborgereStatus())

	return (
		<div>
			<h4>Oppholdsstatus</h4>
			<div className="person-visning_content">
				<TitleValue
					title="Oppholdsstatus"
					value={
						oppholdsrett
							? 'EØS- eller EFTA-opphold'
							: tredjelandsborger
								? 'Tredjelandsborger'
								: null
					}
				/>
				<TitleValue
					title="Type opphold"
					value={showLabel('eosEllerEFTAtypeOpphold', currentOppholdsrettType)}
				/>
				<TitleValue title="Status" value={currentTredjelandsborgereStatus()} />
				<TitleValue
					title="Oppholdstillatelse fra"
					value={formatStringDates(
						tredjelandsborger
							? _.get(opphold, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra')
							: _.get(opphold, `${currentOppholdsrettType}Periode.fra`),
					)}
				/>
				<TitleValue
					title="Oppholdstillatelse til"
					value={formatStringDates(
						tredjelandsborger
							? _.get(opphold, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til')
							: _.get(opphold, `${currentOppholdsrettType}Periode.til`),
					)}
				/>
				<TitleValue
					title="Effektueringsdato"
					value={formatStringDates(
						_.get(opphold, `${currentOppholdsrettType}Effektuering`) ||
							_.get(opphold, 'oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering'),
					)}
				/>
				<TitleValue
					title="Type oppholdstillatelse"
					value={showLabel(
						'oppholdstillatelseType',
						_.get(opphold, 'oppholdSammeVilkaar.oppholdstillatelseType'),
					)}
				/>
				<TitleValue
					title="Vedtaksdato"
					value={formatStringDates(
						_.get(opphold, 'oppholdSammeVilkaar.oppholdstillatelseVedtaksDato'),
					)}
				/>
				<TitleValue
					title="Grunnlag for opphold"
					value={
						oppholdsrett &&
						// @ts-ignore
						showLabel([currentOppholdsrettType], opphold[currentOppholdsrettType])
					}
				/>
				{oppholdstillatelse && <TitleValue title="Har oppholdstillatelse" value="Ja" />}
			</div>
			<AvslagEllerBortfallVisning
				// @ts-ignore
				avslagEllerBortfall={
					opphold?.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum?.avslagEllerBortfall
				}
			/>
		</div>
	)
}
