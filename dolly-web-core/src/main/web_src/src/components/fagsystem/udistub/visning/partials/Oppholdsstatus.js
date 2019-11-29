import React from 'react'
import _get from 'lodash/get'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Oppholdsstatus = ({ oppholdsstatus, oppholdstillatelse }) => {
	if (!oppholdsstatus) return null

	const oppholdsrettTyper = [
		'eosEllerEFTABeslutningOmOppholdsrett',
		'eosEllerEFTAVedtakOmVarigOppholdsrett',
		'eosEllerEFTAOppholdstillatelse'
	]
	const currentOppholdsrettType = oppholdsrettTyper.find(type => oppholdsstatus[type])
	const currentTredjelandsborgereStatus = oppholdsstatus.oppholdSammeVilkaar
		? 'Oppholdstillatelse eller opphold på samme vilkår'
		: oppholdsstatus.uavklart
		? 'Uavklart'
		: oppholdstillatelse === false
		? 'Ikke oppholdstillatalse eller ikke opphold på samme vilkår'
		: null
	const oppholdsrett = Boolean(currentOppholdsrettType)
	const tredjelandsborger = Boolean(currentTredjelandsborgereStatus)

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
					value={
						oppholdsrett && Formatters.showLabel('eosEllerEFTAtypeOpphold', currentOppholdsrettType)
					}
				/>
				<TitleValue title="Status" value={currentTredjelandsborgereStatus} />
				<TitleValue
					title="Oppholdstillatelse fra dato"
					value={Formatters.formatStringDates(
						_get(oppholdsstatus, `${currentOppholdsrettType}Periode.fra`) ||
							_get(oppholdsstatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra')
					)}
				/>
				<TitleValue
					title="Oppholdstillatelse til dato"
					value={Formatters.formatStringDates(
						_get(oppholdsstatus, `${currentOppholdsrettType}Periode.til`) ||
							_get(oppholdsstatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til')
					)}
				/>
				<TitleValue
					title="Effektueringsdato"
					value={Formatters.formatStringDates(
						_get(oppholdsstatus, `${currentOppholdsrettType}Effektuering`) ||
							_get(oppholdsstatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering')
					)}
				/>
				<TitleValue
					title="Type oppholdstillatelse"
					value={Formatters.showLabel(
						'oppholdstillatelseType',
						_get(oppholdsstatus, 'oppholdSammeVilkaar.oppholdstillatelseType')
					)}
				/>
				<TitleValue
					title="Vedtaksdato"
					value={Formatters.formatStringDates(
						_get(oppholdsstatus, 'oppholdSammeVilkaar.oppholdstillatelseVedtaksDato')
					)}
				/>
				<TitleValue
					title="Grunnlag for opphold"
					value={
						oppholdsrett &&
						Formatters.showLabel([currentOppholdsrettType], oppholdsstatus[currentOppholdsrettType])
					}
				/>
			</div>
		</div>
	)
}
