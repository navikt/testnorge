import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { Arbeidsavtaler } from './partials/Arbeidsavtaler'
import { Arbeidsgiver } from './partials/Arbeidsgiver'
import { Fartoy } from './partials/Fartoy'
import { PermisjonPermitteringer } from './partials/PermisjonPermitteringer'
import { AntallTimerForTimeloennet } from './partials/AntallTimerForTimeloennet'
import { Utenlandsopphold } from './partials/Utenlandsopphold'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import { formatDate } from '@/utils/DataFormatter'
import React from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { getFagsystemTimeoutTitle } from '@/components/fagsystem/utils'

type AaregVisningProps = {
	ident?: string
	master?: string
	liste?: Array<MiljoDataListe>
	loading?: boolean
	bestillingIdListe?: Array<string>
	bestillinger?: Array<any>
	tilgjengeligMiljoe?: string
	timedOutFagsystemer?: string[]
}

type MiljoDataListe = {
	miljo: string
	data: Array<Arbeidsforhold>
	feil?: { message?: string }
}

type ArbeidsforholdArray = {
	data?: Array<Arbeidsforhold>
}

type Arbeidsforhold = {
	type?: string
	ansettelsesperiode?: Ansettelsesperiode
	antallTimerForTimeloennet?: Array<unknown>
	arbeidsavtaler?: Array<unknown>
	arbeidsgiver?: ArbeidsgiverProps
	fartoy?: any
	permisjonPermitteringer?: Array<unknown>
	utenlandsopphold?: Array<unknown>
	arbeidsforholdId: string
	sporingsinformasjon?: { opprettetAv?: string }
	varsler?: any[]
}

type ArbeidsgiverProps = {
	type?: string
	organisasjonsnummer?: string
	offentligIdent?: string
}

type Ansettelsesperiode = {
	periode?: Periode
	sluttaarsak?: string
}

type Periode = {
	fom?: string
	tom?: string
}

export const sjekkManglerAaregData = (aaregData: Array<MiljoDataListe>) => {
	return (
		aaregData?.length < 1 ||
		aaregData?.every((miljoData) => !miljoData?.data || miljoData?.data?.length < 1)
	)
}

const Arbeidsforhold = ({ data }: ArbeidsforholdArray) => {
	if (!data) return null
	const sortedData = data
		?.slice()
		?.sort?.((a, b) => parseInt(a.arbeidsforholdId) - parseInt(b.arbeidsforholdId))
	return (
		<DollyFieldArray
			header="Arbeidsforhold"
			getHeader={(d) =>
				`Arbeidsforhold (${d?.arbeidsgiver?.type}: ${d?.arbeidsgiver?.organisasjonsnummer || d?.arbeidsgiver?.offentligIdent})`
			}
			data={sortedData}
			expandable={sortedData.length > 1}
		>
			{(arbeidsforhold, idx) => (
				<React.Fragment>
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Arbeidsforhold-ID" value={arbeidsforhold.arbeidsforholdId} />
						{arbeidsforhold.ansettelsesperiode && (
							<>
								<TitleValue
									title="Arbeidsforhold type"
									value={arbeidsforhold.type}
									kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
								/>
								{arbeidsforhold.ansettelsesperiode.periode && (
									<TitleValue
										title="Ansatt fra"
										value={formatDate(arbeidsforhold.ansettelsesperiode.periode.fom)}
									/>
								)}
								{arbeidsforhold.ansettelsesperiode.periode && (
									<TitleValue
										title="Ansatt til"
										value={formatDate(arbeidsforhold.ansettelsesperiode.periode.tom)}
									/>
								)}
								<TitleValue
									title="Sluttårsak"
									value={arbeidsforhold?.ansettelsesperiode?.sluttaarsak}
									kodeverk={ArbeidKodeverk.SluttaarsakAareg}
								/>
							</>
						)}
					</div>
					{arbeidsforhold.arbeidsgiver && <Arbeidsgiver data={arbeidsforhold.arbeidsgiver} />}
					<Arbeidsavtaler data={arbeidsforhold.arbeidsavtaler} />
					<Fartoy data={arbeidsforhold.fartoy} />
					<AntallTimerForTimeloennet data={arbeidsforhold.antallTimerForTimeloennet} />
					<Utenlandsopphold data={arbeidsforhold.utenlandsopphold} />
					<PermisjonPermitteringer data={arbeidsforhold.permisjonPermitteringer} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const erOpprettetAvDolly = (opprettetAv?: string): boolean =>
	!!(
		opprettetAv &&
		(opprettetAv.includes('testnav') ||
			opprettetAv.includes('srvtestnorge') ||
			opprettetAv.includes('srvappserver') ||
			opprettetAv.includes('aareg-dolly-api'))
	)

export const AaregVisning = ({
	liste,
	loading,
	bestillingIdListe,
	bestillinger,
	tilgjengeligMiljoe,
	timedOutFagsystemer = [],
}: AaregVisningProps) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'AAREG')
	if (loading) return <Loading label="Laster Aareg-data" />
	if (!liste || liste.length === 0) return null
	const miljoerMedData = liste?.map((miljoData) => miljoData?.data?.length > 0 && miljoData?.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))
	const forsteMiljo =
		liste?.find((miljoData) => miljoData?.data?.length > 0)?.miljo || liste?.[0]?.miljo
	const aaregBestillinger = []
	bestillinger?.forEach((best) => {
		best?.bestilling?.aareg?.forEach((arbforh) => aaregBestillinger.push(arbforh))
	})
	const harArbeidsforholdBestilling = aaregBestillinger?.some((best) => best?.arbeidsgiver)
	const arbeidsforhold = liste?.map((item) => ({
		...item,
		data: item?.data
			?.map((data) => (erOpprettetAvDolly(data?.sporingsinformasjon?.opprettetAv) ? data : null))
			?.filter((d) => d),
	}))
	const filteredData =
		tilgjengeligMiljoe && arbeidsforhold?.filter((item) => tilgjengeligMiljoe.includes(item.miljo))
	const manglerArbeidsforholdData =
		arbeidsforhold?.length < 1 || arbeidsforhold?.every((m) => !m?.data || m?.data?.length < 1)
	const arbeidsforholdFeil =
		harArbeidsforholdBestilling && arbeidsforhold?.find((arbforh) => arbforh?.feil)
	return (
		<div>
			<SubOverskrift
				label="Arbeidsforhold"
				iconKind="arbeid"
				isWarning={manglerArbeidsforholdData}
				title={
					timedOutFagsystemer?.includes('AAREG') ? getFagsystemTimeoutTitle('AAREG') : undefined
				}
			/>
			{arbeidsforholdFeil?.feil && manglerArbeidsforholdData ? (
				<StyledAlert variant={'warning'} size={'small'} inline>
					{arbeidsforholdFeil?.feil?.message}
				</StyledAlert>
			) : manglerArbeidsforholdData ? (
				<StyledAlert variant={'warning'} size={'small'} inline>
					Fant ikke arbeidsforhold-data på person
				</StyledAlert>
			) : (
				<ErrorBoundary>
					<MiljoTabs
						bestilteMiljoer={bestilteMiljoer}
						errorMiljoer={errorMiljoer}
						forsteMiljo={forsteMiljo}
						data={filteredData || arbeidsforhold}
					>
						<Arbeidsforhold />
					</MiljoTabs>
				</ErrorBoundary>
			)}
		</div>
	)
}
