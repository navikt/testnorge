import React from 'react'
import _get from 'lodash/get'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { AvslagEllerBortfall, AvslagEllerBortfallVisning } from './AvslagEllerBortfallVisning'
import {
	UtvistMedInnreiseForbud,
	UtvistMedInnreiseForbudVisning
} from './UtvistMedInnreiseForbudVisning'

type Opphold = {
	oppholdsstatus: {
		oppholdSammeVilkaar: {}
		ikkeOppholdstilatelseIkkeVilkaarIkkeVisum: {
			avslagEllerBortfall: AvslagEllerBortfall
			utvistMedInnreiseForbud: UtvistMedInnreiseForbud
			ovrigIkkeOppholdsKategoriArsak: string
		}
		uavklart: boolean
	}
	oppholdstillatelse: boolean
}

export const Oppholdsstatus = (opphold: Opphold) => {
	if (!opphold || !opphold.oppholdsstatus) return null

	const oppholdsstatus = opphold.oppholdsstatus

	const oppholdsrettTyper = [
		'eosEllerEFTABeslutningOmOppholdsrett',
		'eosEllerEFTAVedtakOmVarigOppholdsrett',
		'eosEllerEFTAOppholdstillatelse'
	]
	// @ts-ignore
	const currentOppholdsrettType = oppholdsrettTyper.find(type => oppholdsstatus[type])
	const currentTredjelandsborgereStatus = oppholdsstatus.oppholdSammeVilkaar
		? 'Oppholdstillatelse eller opphold på samme vilkår'
		: oppholdsstatus.uavklart
		? 'Uavklart'
		: 'Ikke oppholdstillatelse eller ikke opphold på samme vilkår'
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
					value={Formatters.showLabel('eosEllerEFTAtypeOpphold', currentOppholdsrettType)}
				/>
				<TitleValue title="Status" value={currentTredjelandsborgereStatus} />
				<TitleValue
					title="Oppholdstillatelse fra"
					value={Formatters.formatStringDates(
						_get(oppholdsstatus, `${currentOppholdsrettType}Periode.fra`) ||
							_get(opphold, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra')
					)}
				/>
				<TitleValue
					title="Oppholdstillatelse til"
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
						// @ts-ignore
						Formatters.showLabel([currentOppholdsrettType], oppholdsstatus[currentOppholdsrettType])
					}
				/>
				<TitleValue
					title="Øvrig årsak"
					value={Formatters.showLabel(
						'ovrigIkkeOppholdsKategoriArsak',
						oppholdsstatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum?.ovrigIkkeOppholdsKategoriArsak
					)}
				/>
			</div>
			<AvslagEllerBortfallVisning
				// @ts-ignore
				avslagEllerBortfall={
					oppholdsstatus?.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum?.avslagEllerBortfall
				}
			/>
			<UtvistMedInnreiseForbudVisning
				// @ts-ignore
				utvistMedInnreiseForbud={
					oppholdsstatus?.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum?.utvistMedInnreiseForbud
				}
			/>
		</div>
	)
}
