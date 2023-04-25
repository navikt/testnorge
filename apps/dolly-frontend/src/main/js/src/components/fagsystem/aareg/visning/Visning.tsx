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
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import { formatDate } from '@/utils/DataFormatter'
import React from 'react'

type AaregVisningProps = {
	ident?: string
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

type AmeldingArray = {
	data?: Array<Amelding>
}

type Amelding = {
	kalendermaaned?: string
	opplysningspliktigOrganisajonsnummer?: string
	virksomheter?: any
	version?: number
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
	return aaregData?.length < 1 || aaregData?.every((miljoData) => miljoData?.data?.length < 1)
}

const getHeader = (data: Arbeidsforhold | Amelding) => {
	return (
		data?.kalendermaaned ||
		`Arbeidsforhold (${data?.arbeidsgiver?.type}: ${
			data.arbeidsgiver.organisasjonsnummer || data.arbeidsgiver.offentligIdent
		})`
	)
}

const Amelding = ({ data }: AmeldingArray) => {
	if (!data) return null

	return (
		<DollyFieldArray
			header="Amelding"
			getHeader={getHeader}
			data={data}
			expandable={data.length > 1}
		>
			{(amelding: Amelding, idx: number) => (
				<React.Fragment>
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Amelding-maaned" value={amelding.kalendermaaned} />
					</div>
				</React.Fragment>
			)}
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
	liste,
	ameldinger,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
}: AaregVisningProps) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'aareg')

	if (loading) {
		return <Loading label="Laster Aareg-data" />
	}
	if (!liste) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerAaregData(liste)

	const miljoerMedData = liste?.map((miljoData) => miljoData?.data?.length > 0 && miljoData?.miljo)

	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))

	const miljoerMedDataAmeldinger = ameldinger?.map(
		(miljoData) => miljoData?.data?.length > 0 && miljoData?.miljo
	)
	const errorMiljoerAmeldinger = bestilteMiljoer?.filter(
		(miljo) => !miljoerMedDataAmeldinger?.includes(miljo)
	)

	const forsteMiljo =
		liste?.find((miljoData) => miljoData?.data?.length > 0)?.miljo || liste?.[0]?.miljo

	const forsteMiljoAmelding =
		ameldinger?.find((miljoData) => miljoData?.data?.length > 0)?.miljo || liste?.[0]?.miljo

	const harAmeldingBestilling = ameldinger?.some((amelding) => amelding?.data?.length > 0)

	const filteredData =
		tilgjengeligMiljoe && liste?.filter((item) => item.miljo === tilgjengeligMiljoe)

	const getArbeidsforhold = () => (
		<div>
			<SubOverskrift label="Arbeidsforhold" iconKind="arbeid" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ margin: '7px' }}>
					Fant ikke arbeidsforhold-data på person
				</Alert>
			) : (
				<ErrorBoundary>
					<MiljoTabs
						bestilteMiljoer={bestilteMiljoer}
						errorMiljoer={errorMiljoer}
						forsteMiljo={forsteMiljo}
						data={filteredData || liste}
					>
						<Arbeidsforhold />
					</MiljoTabs>
				</ErrorBoundary>
			)}
		</div>
	)

	const getAmelding = () => (
		<div>
			<SubOverskrift label="Amelding" iconKind="arbeid" isWarning={!harAmeldingBestilling} />
			{!harAmeldingBestilling ? (
				<Alert variant={'warning'} size={'small'} inline style={{ margin: '7px' }}>
					Fant ikke amelding-data på person
				</Alert>
			) : (
				<ErrorBoundary>
					<MiljoTabs
						bestilteMiljoer={bestilteMiljoer}
						errorMiljoer={errorMiljoerAmeldinger}
						forsteMiljo={forsteMiljoAmelding}
						data={ameldinger}
					>
						<Amelding />
					</MiljoTabs>
				</ErrorBoundary>
			)}
		</div>
	)

	return (
		<>
			{getArbeidsforhold()}
			{getAmelding()}
		</>
	)
}
