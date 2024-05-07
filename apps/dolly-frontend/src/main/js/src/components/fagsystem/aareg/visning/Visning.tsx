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
import { arrayToString, formatDate } from '@/utils/DataFormatter'
import _ from 'lodash'
import React from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'

type AaregVisningProps = {
	ident?: string
	master?: string
	liste?: Array<MiljoDataListe>
	ameldinger?: Array<any>
	loading?: boolean
	bestillingIdListe?: Array<string>
	tilgjengeligMiljoe?: string
}

type MiljoDataListe = {
	miljo: string
	data: Array<Arbeidsforhold>
}

type ArbeidsforholdArray = {
	data?: Array<Arbeidsforhold>
}

type Amelding = {
	kalendermaaned?: string
	opplysningspliktigOrganisajonsnummer?: string
	virksomheter?: AmeldingUnderenhet[]
	version?: number
}

type AmeldingUnderenhet = {
	organisajonsnummer?: string //Skrivefeilen kommer fra endepunkt
	personer?: Personer[]
}

type Personer = {
	ident?: string
	arbeidsforhold?: AmeldingArbeidsforhold[]
}

type AmeldingArbeidsforhold = {
	arbeidsforholdId: string
	typeArbeidsforhold: string
	startdato: Date
	sluttdato: Date
	antallTimerPerUke: number
	yrke: string
	arbeidstidsordning: string
	stillingsprosent: string
	sisteLoennsendringsdato: Date
	permisjoner: any[]
	fartoey: any
	inntekter: any[]
	avvik: any[]
	historikk: boolean
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
	arbeidsforholdId?: string
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

export const sjekkManglerAaregData = (aaregData) => {
	return (
		aaregData?.length < 1 ||
		aaregData?.every((miljoData) => !miljoData?.data || miljoData?.data?.length < 1)
	)
}

const getHeader = (data: Arbeidsforhold | Amelding) => {
	return (
		data?.kalendermaaned ||
		`Arbeidsforhold (${data?.arbeidsgiver?.type}: ${
			data.arbeidsgiver.organisasjonsnummer || data.arbeidsgiver.offentligIdent
		})`
	)
}
const AmeldingUnderenhet = ({ data, ident }: any) => {
	if (!data?.virksomheter) return null

	return (
		<DollyFieldArray data={data?.virksomheter} nested>
			{(underenhet: AmeldingUnderenhet, idx: number) => {
				const gjeldendePerson = underenhet.personer?.find(
					(person) => person.ident === ident.toString(),
				)
				const arbeidsforhold = gjeldendePerson?.arbeidsforhold?.[0]
				return (
					<React.Fragment>
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Organisasjon" value={underenhet?.organisajonsnummer} />
							<TitleValue title="Ansatt fra" value={formatDate(arbeidsforhold?.startdato)} />
							<TitleValue title="Sluttdato" value={formatDate(arbeidsforhold?.sluttdato)} />
							<TitleValue
								title="Avtalte arbeidstimer per uke"
								value={arbeidsforhold?.antallTimerPerUke}
							/>
							<TitleValue title="Arbeidsforhold-ID" value={arbeidsforhold?.arbeidsforholdId} />
							<TitleValue
								title="Arbeidstidsordning"
								value={arbeidsforhold?.arbeidstidsordning}
								kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
							/>
							<TitleValue title="Stillingsprosent" value={arbeidsforhold?.stillingsprosent} />
							<TitleValue
								title="Type arbeidsforhold"
								value={arbeidsforhold?.typeArbeidsforhold}
								kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
							/>
							<TitleValue
								title="Yrke"
								value={arbeidsforhold?.yrke}
								kodeverk={ArbeidKodeverk.Yrker}
							/>
							<TitleValue title="Avvik" value={arrayToString(arbeidsforhold?.avvik)} />
						</div>
						<Fartoy data={arbeidsforhold?.fartoey} />

						<PermisjonPermitteringer data={arbeidsforhold?.permisjoner} />
					</React.Fragment>
				)
			}}
		</DollyFieldArray>
	)
}
const Amelding = ({ data, ident }: any) => {
	if (!data || data?.length === 0) return null

	const ameldingerSortedByKalendermaaned = _.sortBy(data, 'kalendermaaned')

	return (
		<DollyFieldArray
			header="A-melding"
			getHeader={(amelding) => formatDate(amelding?.kalendermaaned)}
			data={ameldingerSortedByKalendermaaned}
			expandable={ameldingerSortedByKalendermaaned.length > 1}
		>
			{(amelding: Amelding, idx: number) => {
				return (
					<React.Fragment>
						<div className="person-visning_content" key={idx}>
							<TitleValue
								title="Opplysningspliktig org"
								value={amelding.opplysningspliktigOrganisajonsnummer}
							/>
							<TitleValue title="Arbeidstaker" value={ident} />
							<div className="flexbox--full-width">
								<h3>Arbeidsforhold</h3>
								<AmeldingUnderenhet data={amelding} ident={ident} />
							</div>
						</div>
					</React.Fragment>
				)
			}}
		</DollyFieldArray>
	)
}

const Arbeidsforhold = ({ data }: ArbeidsforholdArray) => {
	if (!data) return null

	const sortedData = data
		?.slice()
		?.sort((a, b) => parseInt(a.arbeidsforholdId) - parseInt(b.arbeidsforholdId))

	return (
		<DollyFieldArray
			header="Arbeidsforhold"
			getHeader={getHeader}
			data={sortedData}
			expandable={sortedData.length > 1}
		>
			{(arbeidsforhold: Arbeidsforhold, idx: number) => (
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

					<Arbeidsgiver data={arbeidsforhold.arbeidsgiver} />

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

export const AaregVisning = ({
	ident,
	master,
	liste,
	ameldinger,
	loading,
	bestillingIdListe,
	bestillinger,
	tilgjengeligMiljoe,
}: AaregVisningProps) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'AAREG')

	if (loading) {
		return <Loading label="Laster Aareg-data" />
	}
	if (!liste && !ameldinger) {
		return null
	}

	const miljoerMedData = liste?.map((miljoData) => miljoData?.data?.length > 0 && miljoData?.miljo)

	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))

	const miljoerMedDataAmeldinger = ameldinger?.map(
		(miljoData) => miljoData?.data?.length > 0 && miljoData?.miljo,
	)
	const errorMiljoerAmeldinger = bestilteMiljoer?.filter(
		(miljo) => !miljoerMedDataAmeldinger?.includes(miljo),
	)

	const forsteMiljo =
		liste?.find((miljoData) => miljoData?.data?.length > 0)?.miljo || liste?.[0]?.miljo

	const forsteMiljoAmelding =
		ameldinger?.find((miljoData) => miljoData?.data?.length > 0)?.miljo || liste?.[0]?.miljo

	const aaregBestillinger = []
	bestillinger?.forEach((best) => {
		best?.bestilling?.aareg?.forEach((arbforh) => aaregBestillinger.push(arbforh))
	})
	const harArbeidsforholdBestilling = aaregBestillinger?.some((best) => best?.arbeidsgiver)
	const harAmeldingBestilling = aaregBestillinger?.some(
		(best) => best?.amelding && best?.amelding?.length > 0,
	)

	const arbeidsforhold = liste?.map((item) => {
		return {
			...item,
			data: item?.data
				?.map((data) => {
					return data?.sporingsinformasjon?.opprettetAv?.includes('testnav') ||
						data?.sporingsinformasjon?.opprettetAv?.includes('srvappserver')
						? data
						: null
				})
				?.filter((data) => data),
		}
	})

	const filteredData =
		tilgjengeligMiljoe && arbeidsforhold?.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	const manglerArbeidsforholdData = sjekkManglerAaregData(arbeidsforhold)
	const arbeidsforholdFeil =
		harArbeidsforholdBestilling && arbeidsforhold?.find((arbforh) => arbforh?.feil)

	const manglerAmeldingData = sjekkManglerAaregData(ameldinger)
	const ameldingFeil = harAmeldingBestilling && ameldinger?.find((amelding) => amelding?.feil)

	const getArbeidsforhold = () => {
		return (
			<div>
				<SubOverskrift
					label="Arbeidsforhold"
					iconKind="arbeid"
					isWarning={manglerArbeidsforholdData}
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

	const getAmelding = (ident) => {
		return (
			<div>
				<SubOverskrift label="A-melding" iconKind="arbeid" isWarning={manglerAmeldingData} />
				{ameldingFeil?.feil && manglerAmeldingData ? (
					<StyledAlert variant={'warning'} size={'small'} inline>
						{ameldingFeil?.feil?.message}
					</StyledAlert>
				) : manglerAmeldingData ? (
					<StyledAlert variant={'warning'} size={'small'} inline>
						Fant ikke amelding-data på person
					</StyledAlert>
				) : (
					<ErrorBoundary>
						<MiljoTabs
							bestilteMiljoer={bestilteMiljoer}
							errorMiljoer={errorMiljoerAmeldinger}
							forsteMiljo={forsteMiljoAmelding}
							data={ameldinger}
						>
							<Amelding data={ameldinger} ident={ident} />
						</MiljoTabs>
					</ErrorBoundary>
				)}
			</div>
		)
	}

	return (
		<>
			{(harArbeidsforholdBestilling || master === 'PDL') && getArbeidsforhold()}
			{harAmeldingBestilling && getAmelding(ident)}
		</>
	)
}
