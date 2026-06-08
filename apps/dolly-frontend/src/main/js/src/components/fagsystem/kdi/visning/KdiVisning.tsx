import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Loading from '@/components/ui/loading/Loading'
import { Alert } from '@navikt/ds-react'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDateTimeWithSeconds, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { useFengsel } from '@/utils/hooks/useInstitusjon'

// TODO: Fiks denne
export const sjekkManglerKdiData = (kdiData) => {
	return false
	// return kdiData?.length < 1 || kdiData?.every((miljoData) => miljoData.data?.length < 1)
}

export const KdiMelding = ({ melding, id, annulleringListe, tidspunktLabel = 'Tidspunkt' }) => {
	const annullering = annulleringListe?.find((a) => a.annullertMeldingId === melding.meldingId)

	const { fengsler, loading, error } = useFengsel()
	const fengselLabel = fengsler
		? `${melding.organisasjonsnummer} - ${fengsler[melding.organisasjonsnummer]}`
		: melding.organisasjonsnummer

	return (
		<>
			<div className="person-visning_content" key={id}>
				{/*TODO: hendelseId?*/}
				{/*TODO: meldingId?*/}
				<TitleValue
					title="Publiseringstidspunkt"
					value={formatDateTimeWithSeconds(melding.publiseringstidspunkt)}
				/>
				<TitleValue title="Kategori" value={showLabel('kdiKategori', melding.kategori)} />
				<TitleValue title="Organisasjonsnummer" value={fengselLabel} />
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
				{/*	TODO: innmeldingHendelseId?*/}
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

export const KdiVisning = ({ data, loading, harKdiBestilling }) => {
	console.log('data: ', data) //TODO - SLETT MEG

	if (!data && !harKdiBestilling) {
		return null
	}

	if (loading) {
		return <Loading label="Laster KDI-meldinger ..." />
	}
	console.log('data: ', data) //TODO - SLETT MEG
	console.log('harKdiBestilling: ', harKdiBestilling) //TODO - SLETT MEG
	const manglerFagsystemdata = harKdiBestilling && !data

	const {
		innsettelse,
		loeslatelse,
		avbruddStart,
		avbruddSlutt,
		forventetLoeslatelse,
		annullering,
	} = data

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
					{innsettelse?.length > 0 && (
						<DollyFieldArray data={innsettelse} header="Innsettelse" nested>
							{(melding, idx) => (
								<KdiMelding
									melding={melding}
									id={'innsettelse' + idx}
									annulleringListe={annullering}
									tidspunktLabel="Innsettelsestidspunkt"
								/>
							)}
						</DollyFieldArray>
					)}
					{loeslatelse?.length > 0 && (
						<DollyFieldArray data={loeslatelse} header="Løslatelse" nested>
							{(melding, idx) => (
								<KdiMelding
									melding={melding}
									id={'loeslatelse' + idx}
									annulleringListe={annullering}
									tidspunktLabel="Løslatelsestidspunkt"
								/>
							)}
						</DollyFieldArray>
					)}
					{avbruddStart?.length > 0 && (
						<DollyFieldArray data={avbruddStart} header="Avbrudd start" nested>
							{(melding, idx) => (
								<KdiMelding
									melding={melding}
									id={'avbruddStart' + idx}
									annulleringListe={annullering}
									tidspunktLabel="Tidspunkt for start på straffeavbrudd"
								/>
							)}
						</DollyFieldArray>
					)}
					{avbruddSlutt?.length > 0 && (
						<DollyFieldArray data={avbruddSlutt} header="Avbrudd slutt" nested>
							{(melding, idx) => (
								<KdiMelding
									melding={melding}
									id={'avbruddSlutt' + idx}
									annulleringListe={annullering}
									tidspunktLabel="Tidspunkt for slutt på straffeavbrudd"
								/>
							)}
						</DollyFieldArray>
					)}
					{forventetLoeslatelse?.length > 0 && (
						<DollyFieldArray data={forventetLoeslatelse} header="Forventet løslatelse" nested>
							{(melding, idx) => (
								<KdiMelding
									melding={melding}
									id={'forventetLoeslatelse' + idx}
									annulleringListe={annullering}
									tidspunktLabel="Forventet løslatt tidspunkt"
								/>
							)}
						</DollyFieldArray>
					)}
				</ErrorBoundary>
			)}
		</div>
	)
}
