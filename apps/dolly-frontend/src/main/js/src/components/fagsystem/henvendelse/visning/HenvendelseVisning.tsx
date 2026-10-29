import React from 'react'
import { InlineMessage } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import Panel from '@/components/ui/panel/Panel'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { codeToNorskLabel, formatDateTime, oversettBoolean } from '@/utils/DataFormatter'
import { HenvendelseKodeverk } from '@/config/kodeverk'
import { useHenvendelseTemagrupper } from '@/utils/hooks/useHenvendelse'
import { useAlleNavEnheter } from '@/utils/hooks/useNorg2'
import {
	HenvendelseInfo,
	HenvendelseJournalpost,
	HenvendelseMarkering,
	HenvendelseMeldingInfo,
} from '@/components/fagsystem/henvendelse/HenvendelseTypes'

interface HenvendelseVisningProps {
	data?: HenvendelseInfo[]
	loading: boolean
	harBestilling: boolean
}

interface HenvendelseListeProps {
	data: HenvendelseInfo[]
}

const getHeader = (data: HenvendelseInfo) => {
	return `Henvendelse (${formatDateTime(data.opprettetDato)})`
}

export const HenvendelseListe = ({ data }: HenvendelseListeProps) => {
	const { temagrupper } = useHenvendelseTemagrupper()
	const { alleNavEnheter } = useAlleNavEnheter()

	const getTemagruppeLabel = (kode?: string) =>
		temagrupper.find((temagruppe) => temagruppe.value === kode)?.label ?? kode

	const getEnhetLabel = (enhetNr?: string) =>
		alleNavEnheter.find((enhet) => enhet.value === enhetNr)?.label ?? enhetNr

	return (
		<div className="person-visning_content">
			<DollyFieldArray
				header={'Henvendelse'}
				getHeader={data.length > 3 ? getHeader : undefined}
				data={data}
				expandable={data.length > 3}
			>
				{(henvendelse: HenvendelseInfo, idx: number) => (
					<React.Fragment key={idx}>
						<TitleValue title="Type" value={codeToNorskLabel(henvendelse.henvendelseType)} />
						<TitleValue title="Opprettet" value={formatDateTime(henvendelse.opprettetDato)} />
						<TitleValue title="Avsluttet" value={formatDateTime(henvendelse.avsluttetDato)} />
						<TitleValue title="Kasseringsdato" value={formatDateTime(henvendelse.kasseringsDato)} />
						<TitleValue title="Avsluttet av" value={henvendelse.avsluttetAv} />
						<TitleValue title="Satt til sladding av" value={henvendelse.sattTilSladdingAv} />
						<TitleValue title="Sladding" value={oversettBoolean(henvendelse.sladding)} />
						<TitleValue title="Feilsendt" value={oversettBoolean(henvendelse.feilsendt)} />
						<TitleValue title="Kjede-ID" value={henvendelse.kjedeId} />
						<TitleValue
							title="Temagruppe"
							value={getTemagruppeLabel(henvendelse.gjeldendeTemagruppe)}
						/>
						<TitleValue
							title="Tema"
							value={henvendelse.gjeldendeTema}
							kodeverk={HenvendelseKodeverk.Tema}
						/>
						{henvendelse.journalposter && henvendelse.journalposter.length > 0 && (
							<DollyFieldArray header="Journalposter" data={henvendelse.journalposter} nested>
								{(journalpost: HenvendelseJournalpost, journalpostIdx: number) => (
									<React.Fragment key={journalpostIdx}>
										<TitleValue title="Journalført av" value={journalpost.journalforerNavIdent} />
										<TitleValue
											title="Journalførende enhet"
											value={getEnhetLabel(journalpost.journalforendeEnhet)}
										/>
										<TitleValue
											title="Journalført"
											value={formatDateTime(journalpost.journalfortDato)}
										/>
										<TitleValue
											title="Tema"
											value={journalpost.journalfortTema}
											kodeverk={HenvendelseKodeverk.Tema}
										/>
										<TitleValue title="Fagsak-ID" value={journalpost.fagsakId} />
										<TitleValue title="Fagsaksystem" value={journalpost.fagsaksystem} />
										<TitleValue title="Journalpost-ID" value={journalpost.journalpostId} />
									</React.Fragment>
								)}
							</DollyFieldArray>
						)}
						{henvendelse.meldinger && henvendelse.meldinger.length > 0 && (
							<DollyFieldArray header="Meldinger" data={henvendelse.meldinger} nested>
								{(melding: HenvendelseMeldingInfo, meldingIdx: number) => (
									<React.Fragment key={meldingIdx}>
										<TitleValue title="Fritekst" value={melding.fritekst} />
										<TitleValue title="Sendt" value={formatDateTime(melding.sendtDato)} />
										<TitleValue title="Lest" value={formatDateTime(melding.lestDato)} />
										<TitleValue title="Kanal" value={codeToNorskLabel(melding.kanal)} />
										<TitleValue title="Meldings-ID" value={melding.meldingsId} />
										<TitleValue title="Sladding" value={oversettBoolean(melding.sladding)} />
										<TitleValue
											title="Fra identtype"
											value={codeToNorskLabel(melding.fra?.identType)}
										/>
										<TitleValue title="Fra ident" value={melding.fra?.ident} />
										<TitleValue title="Fra enhet" value={getEnhetLabel(melding.fra?.navEnhet)} />
									</React.Fragment>
								)}
							</DollyFieldArray>
						)}
						{henvendelse.markeringer && henvendelse.markeringer.length > 0 && (
							<DollyFieldArray header="Markeringer" data={henvendelse.markeringer} nested>
								{(markering: HenvendelseMarkering, markeringIdx: number) => (
									<React.Fragment key={markeringIdx}>
										<TitleValue title="Type" value={codeToNorskLabel(markering.markeringstype)} />
										<TitleValue title="Markert" value={formatDateTime(markering.markertDato)} />
										<TitleValue title="Markert av" value={markering.markertAv} />
									</React.Fragment>
								)}
							</DollyFieldArray>
						)}
					</React.Fragment>
				)}
			</DollyFieldArray>
		</div>
	)
}

export const HenvendelseVisning = ({ data, loading, harBestilling }: HenvendelseVisningProps) => {
	if (loading) {
		return <Loading label="Laster henvendelser ..." />
	}

	if (!harBestilling) {
		return null
	}

	const manglerFagsystemdata = !data || data.length === 0

	return (
		<div>
			<SubOverskrift label="Henvendelser" iconKind="salesforce" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<InlineMessage status="warning" size="small" style={{ marginBottom: '20px' }}>
					Fant ikke henvendelser på person
				</InlineMessage>
			) : (
				<ErrorBoundary>
					{data.length > 7 ? (
						// @ts-ignore
						<Panel heading={`Henvendelser (${data.length})`}>
							<HenvendelseListe data={data} />
						</Panel>
					) : (
						<HenvendelseListe data={data} />
					)}
				</ErrorBoundary>
			)}
		</div>
	)
}
