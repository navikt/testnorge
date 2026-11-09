import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Loading from '@/components/ui/loading/Loading'
import { Alert } from '@navikt/ds-react'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import {
	codeToNorskLabel,
	formatDateTimeWithSeconds,
	oversettBoolean,
	showLabel,
} from '@/utils/DataFormatter'
import { useFengsel } from '@/utils/hooks/useInstitusjon'
import {
	getTidspunktLabel,
	grupperKdiHendelser,
	KdiHendelse,
} from '@/components/fagsystem/kdi/utils'
import { KdiMeldingProps, KdiProps } from '@/components/fagsystem/kdi/form/partials/types'

type KdiMeldingVisningProps = {
	melding: KdiMeldingProps
	id: number
	annulleringListe?: Array<KdiMeldingProps>
	tittel?: string
}

type KdiVisningProps = {
	data?: KdiProps
	loading: boolean
	harKdiBestilling: boolean
}

export const sjekkManglerKdiData = (kdiData: KdiProps) => {
	return (
		!kdiData ||
		((!kdiData.annullering || kdiData.annullering?.length < 1) &&
			(!kdiData.avbruddSlutt || kdiData.avbruddSlutt?.length < 1) &&
			(!kdiData.avbruddStart || kdiData.avbruddStart?.length < 1) &&
			(!kdiData.forventetLoeslatelse || kdiData.forventetLoeslatelse?.length < 1) &&
			(!kdiData.innsettelse || kdiData.innsettelse?.length < 1) &&
			(!kdiData.loeslatelse || kdiData.loeslatelse?.length < 1))
	)
}

export const KdiMelding = ({ melding, id, annulleringListe, tittel }: KdiMeldingVisningProps) => {
	const annullering = annulleringListe?.find((a) => a.annullertMeldingId === melding.meldingId)

	const { fengsler } = useFengsel()
	const fengselLabel = fengsler
		? `${melding.organisasjonsnummer} - ${fengsler[melding.organisasjonsnummer]}`
		: melding.organisasjonsnummer

	const tidspunktLabel = getTidspunktLabel(melding.type)

	return (
		<>
			{tittel && <h4 style={{ marginTop: '0px' }}>{tittel}</h4>}
			<div className="person-visning_content" key={id} style={{ marginBottom: 0 }}>
				<TitleValue
					title="Publiseringstidspunkt"
					value={formatDateTimeWithSeconds(melding.publiseringstidspunkt)}
				/>
				<TitleValue title="Kategori" value={showLabel('kdiKategori', melding.kategori)} />
				<TitleValue
					title="Organisasjonsnummer"
					value={melding.organisasjonsnummer ? fengselLabel : null}
				/>
				<TitleValue title={tidspunktLabel} value={formatDateTimeWithSeconds(melding.tidspunkt)} />
				<TitleValue
					title="Er overført til utenlandsk fengsel"
					value={oversettBoolean(melding.erOverfoertTilUtlandskfengsel)}
				/>
				<TitleValue
					title="Er overført til varetekt med elektronisk kontroll"
					value={oversettBoolean(melding.erOverfoertTilVaretektMedElektroniskKontroll)}
				/>
				<TitleValue
					title="Forventet tidspunkt for slutt på straffeavbrudd"
					value={formatDateTimeWithSeconds(melding.forventetAvbruddSluttTidspunkt)}
				/>
			</div>
			{annullering && (
				<>
					<h4 style={{ marginTop: '0px' }}>Annullering</h4>
					<div className="person-visning_content">
						<TitleValue
							title="Publiseringstidspunkt"
							value={formatDateTimeWithSeconds(annullering.publiseringstidspunkt)}
						/>
					</div>
				</>
			)}
		</>
	)
}

export const KdiVisning = ({ data, loading, harKdiBestilling }: KdiVisningProps) => {
	if (!data && !harKdiBestilling) {
		return null
	}

	if (loading) {
		return <Loading label="Laster KDI-meldinger ..." />
	}

	const annullering = data?.annullering ?? []

	const hendelser = grupperKdiHendelser(data)
	const antallMeldinger = hendelser.reduce(
		(antall, hendelse) => antall + hendelse.meldinger.length,
		0,
	)
	const manglerFagsystemdata = harKdiBestilling && (!data || hendelser.length < 1)

	return (
		<div>
			<SubOverskrift
				label="KDI-meldinger"
				iconKind="institusjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ingen KDI-meldinger på person
				</Alert>
			) : (
				<ErrorBoundary>
					<DollyFieldArray
						data={hendelser}
						getHeader={(hendelse: KdiHendelse) => codeToNorskLabel(hendelse.type)}
						expandable={antallMeldinger > 5}
					>
						{(hendelse: KdiHendelse) => (
							<DollyFieldArray data={hendelse.meldinger} nested>
								{(melding: KdiMeldingProps, idx: number) => (
									<KdiMelding melding={melding} id={idx} annulleringListe={annullering} />
								)}
							</DollyFieldArray>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</div>
	)
}
