import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export type AvslagEllerBortfall = {
	avslagEllerBortfall: {
		avgjorelsesDato: Date
		avslagOppholdsrettBehandlet: string
		avslagOppholdstillatelseBehandletGrunnlagEOS: string
		avslagOppholdstillatelseBehandletGrunnlagOvrig: string
	}
}

export const AvslagEllerBortfallVisning = ({ avslagEllerBortfall }: AvslagEllerBortfall) =>
	avslagEllerBortfall && Object.values(avslagEllerBortfall).some(item => item !== null) ? (
		<>
			<h4>Avslag eller bortfall</h4>
			<div className="person-visning_content">
				<TitleValue
					title="Avgjørelsesdato"
					value={Formatters.formatStringDates(avslagEllerBortfall.avgjorelsesDato)}
				/>
				<TitleValue
					title="Avslag Oppholdsrett"
					value={Formatters.showLabel(
						'avslagOppholdsrettBehandlet',
						avslagEllerBortfall.avslagOppholdsrettBehandlet
					)}
				/>
				<TitleValue
					title="Avslag grunnlag EØS"
					value={Formatters.showLabel(
						'avslagGrunnlagTillatelseGrunnlagEOS',
						avslagEllerBortfall.avslagOppholdstillatelseBehandletGrunnlagEOS
					)}
				/>
				<TitleValue
					title="Avslag grunnlag øvrig"
					value={Formatters.showLabel(
						'avslagGrunnlagOverig',
						avslagEllerBortfall.avslagOppholdstillatelseBehandletGrunnlagOvrig
					)}
				/>
			</div>
		</>
	) : null
