import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { InntektVisning } from './partials/InntektVisning'
import { FradragVisning } from './partials/FradragVisning'
import { ForskuddstrekkVisning } from './partials/ForskuddstrekkVisning'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { formatDateTime, formatMonth } from '@/utils/DataFormatter'
import Panel from '@/components/ui/panel/Panel'
import { Alert } from '@navikt/ds-react'
import React, { useMemo, useState } from 'react'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'
import { SlettInntektsinformasjon } from '@/components/fagsystem/inntektstub/visning/SlettInntektsinformasjon'

type InntekstubVisning = {
	liste?: Array<Inntektsinformasjon>
	loading?: boolean
	harInntektstubBestilling?: boolean
}

type InfoProps = {
	sortedData: Array<Inntektsinformasjon>
	numInntekter: number
}

type Inntektsinformasjon = {
	aarMaaned: string
	rapporteringsdato: string
	opplysningspliktig: string
	virksomhet: string
	inntektsliste: Array<unknown>
	fradragsliste?: Array<unknown>
	forskuddstrekksliste?: Array<unknown>
	versjon?: number
}

const getHeader = (data: Inntektsinformasjon) => {
	return `Inntektsinformasjon (${formatMonth(data.aarMaaned)}) ${
		data.versjon ? ` - historikk (versjon ${data.versjon})` : ' '
	}`
}

const InntektsinformasjonVisning = ({ sortedData, numInntekter }: InfoProps) => {
	const virksomheter = useMemo(() => sortedData.map((data) => data.virksomhet), [sortedData])
	const opplysningspliktigeOrg = useMemo(
		() => sortedData.map((data) => data.opplysningspliktig),
		[sortedData],
	)
	const {
		organisasjoner: virksomhetInfo,
		loading: loadingVirksomheter,
		error: errorVirksomheter,
	} = useOrganisasjonForvalter(virksomheter)
	const {
		organisasjoner: opplysningspliktigInfo,
		loading: loadingOpplysninspliktige,
		error: errorOpplysningspliktige,
	} = useOrganisasjonForvalter(opplysningspliktigeOrg)

	const [slettIndex, setSlettIndex] = useState<number | null>(null)

	if (loadingVirksomheter || loadingOpplysninspliktige) {
		return <Loading label="Laster organisasjonsdata" />
	} else if (errorVirksomheter || errorOpplysningspliktige) {
		return (
			<Alert variant={'error'} size={'small'} inline style={{ marginBottom: '20px' }}>
				Feil ved henting av organisasjonsdata:{' '}
				{errorOpplysningspliktige?.message || errorVirksomheter?.message}
			</Alert>
		)
	}

	return (
		<>
			<DollyFieldArray
				header="Inntektsinformasjon"
				getHeader={getHeader}
				data={sortedData}
				expandable={numInntekter > 1}
				handleRemove={(index: number) => setSlettIndex(index)}
				deleteButtonTitle="Slett inntektsinformasjon"
			>
				{(inntektsinformasjon: Inntektsinformasjon, index) => {
					const virksomhetNavn =
						virksomhetInfo?.[index]?.q1?.organisasjonsnavn ||
						virksomhetInfo?.[index]?.q2?.organisasjonsnavn
					const opplysningspliktigNavn =
						opplysningspliktigInfo?.[index]?.q1?.organisasjonsnavn ||
						opplysningspliktigInfo?.[index]?.q2?.organisasjonsnavn
					return (
						<React.Fragment>
							<div className="person-visning_content">
								<TitleValue title="År/måned" value={formatMonth(inntektsinformasjon.aarMaaned)} />
								<TitleValue
									title="Rapporteringstidspunkt"
									value={formatDateTime(inntektsinformasjon.rapporteringsdato)}
								/>
								<TitleValue
									title="Virksomhet"
									value={`${inntektsinformasjon?.virksomhet} - ${virksomhetNavn}`}
								/>
								<TitleValue
									title="Opplysningspliktig"
									value={`${inntektsinformasjon.opplysningspliktig} - ${opplysningspliktigNavn}`}
								/>
							</div>
							<InntektVisning data={inntektsinformasjon.inntektsliste} />
							<FradragVisning data={inntektsinformasjon.fradragsliste} />
							<ForskuddstrekkVisning data={inntektsinformasjon.forskuddstrekksliste} />
						</React.Fragment>
					)
				}}
			</DollyFieldArray>
			<SlettInntektsinformasjon
				open={slettIndex !== null}
				onClose={() => setSlettIndex(null)}
				inntektsinformasjon={slettIndex !== null ? sortedData[slettIndex] : undefined}
			/>
		</>
	)
}

export const InntektstubVisning = ({
	liste,
	loading,
	harInntektstubBestilling,
}: InntekstubVisning) => {
	if (loading && !liste) {
		return <Loading label="Laster Inntektstub-data" />
	}
	if ((!liste || liste.length < 1) && !harInntektstubBestilling) {
		return null
	}

	const foersteAar = liste?.[0]?.aarMaaned?.substring(0, 4)

	const sortedData = liste?.slice().reverse()
	const numInntekter = sortedData?.length

	const sisteAar = sortedData?.[0]?.aarMaaned?.substring(0, 4)
	const inntektsperiode = foersteAar === sisteAar ? foersteAar : `${foersteAar} - ${sisteAar}`

	const manglerFagsystemdata = harInntektstubBestilling && (!sortedData || sortedData?.length < 1)

	return (
		<div>
			<SubOverskrift
				label="A-ordningen (Inntektstub)"
				iconKind="pengesekk"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke inntekt-data på person
				</Alert>
			) : (
				<ErrorBoundary>
					{numInntekter && numInntekter > 5 ? (
						// @ts-ignore
						<Panel heading={`Inntektsinsinformasjon (${inntektsperiode})`}>
							<InntektsinformasjonVisning sortedData={sortedData} numInntekter={numInntekter} />
						</Panel>
					) : (
						<InntektsinformasjonVisning sortedData={sortedData} numInntekter={numInntekter} />
					)}
				</ErrorBoundary>
			)}
		</div>
	)
}
