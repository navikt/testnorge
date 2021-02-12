import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export type AvslagEllerBortfall = {
	avslagEllerBortfall: {
		avgjorelsesDato: string
		avslagGrunnlagOverig: string
		avslagGrunnlagTillatelseGrunnlagEOS: string
		avslagOppholdsrettBehandlet: string
		avslagOppholdstillatelseBehandletGrunnlagEOS: string
		avslagOppholdstillatelseBehandletGrunnlagOvrig: string
		avslagOppholdstillatelseBehandletUtreiseFrist: string
		avslagOppholdstillatelseUtreiseFrist: string
		bortfallAvPOellerBOSDato: string
		tilbakeKallUtreiseFrist: string
		formeltVedtakUtreiseFrist: string
		tilbakeKallVirkningsDato: string
	}
}

export const AvslagEllerBortfallVisning = ({ avslagEllerBortfall }: AvslagEllerBortfall) =>
	avslagEllerBortfall && Object.values(avslagEllerBortfall).some(item => item !== null) ? (
		<>
			<h4>Avslag eller bortfall</h4>
			<div className="person-visning_content">
				<TitleValue
					title="Avgjørelsesdato"
					value={Formatters.formatDate(avslagEllerBortfall.avgjorelsesDato)}
				/>
				<TitleValue
					title="Avslag grunnlag øvrig"
					value={Formatters.showLabel(
						'avslagGrunnlagOverig',
						avslagEllerBortfall.avslagGrunnlagOverig
					)}
				/>
				<TitleValue
					title="Tillatelsesgrunnlag"
					value={Formatters.showLabel(
						'avslagGrunnlagTillatelseGrunnlagEOS',
						avslagEllerBortfall.avslagGrunnlagTillatelseGrunnlagEOS
					)}
				/>
				<TitleValue
					title="Oppholdsrett behandlet"
					value={Formatters.showLabel(
						'avslagOppholdsrettBehandlet',
						avslagEllerBortfall.avslagOppholdsrettBehandlet
					)}
				/>
				<TitleValue
					title="Oppholdsrett grunnlag"
					value={Formatters.showLabel(
						'avslagGrunnlagTillatelseGrunnlagEOS',
						avslagEllerBortfall.avslagOppholdstillatelseBehandletGrunnlagEOS
					)}
				/>
				<TitleValue
					title="Behandlet utreisefrist"
					value={Formatters.formatDate(
						avslagEllerBortfall.avslagOppholdstillatelseBehandletUtreiseFrist
					)}
				/>
				<TitleValue
					title="Utreisefrist"
					value={Formatters.formatDate(avslagEllerBortfall.avslagOppholdstillatelseUtreiseFrist)}
				/>
				<TitleValue
					title="Bortfallsdato"
					value={Formatters.formatDate(avslagEllerBortfall.bortfallAvPOellerBOSDato)}
				/>
				<TitleValue
					title="Tilbakekall utreisefrist"
					value={Formatters.formatDate(avslagEllerBortfall.tilbakeKallUtreiseFrist)}
				/>
				<TitleValue
					title="Vedtak utreisefrist"
					value={Formatters.formatDate(avslagEllerBortfall.formeltVedtakUtreiseFrist)}
				/>
				<TitleValue
					title="Tilbakekall virkningsdato"
					value={Formatters.formatDate(avslagEllerBortfall.tilbakeKallVirkningsDato)}
				/>
			</div>
		</>
	) : null
