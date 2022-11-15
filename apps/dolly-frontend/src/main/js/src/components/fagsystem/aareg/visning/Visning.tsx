import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Arbeidsavtaler } from './partials/Arbeidsavtaler'
import { Arbeidsgiver } from './partials/Arbeidsgiver'
import { Fartoy } from './partials/Fartoy'
import { PermisjonPermitteringer } from './partials/PermisjonPermitteringer'
import { AntallTimerForTimeloennet } from './partials/AntallTimerForTimeloennet'
import { Utenlandsopphold } from './partials/Utenlandsopphold'
import { ArbeidKodeverk } from '~/config/kodeverk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
// import { ArbeidsforholdMiljoeInfo } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/ArbeidsforholdMiljoeInfo'
import { MiljoTabs } from '~/components/ui/miljoTabs/MiljoTabs'

type AaregVisningProps = {
	ident?: string
	liste?: Array<Arbeidsforhold>
	loading?: boolean
	bestilteMiljoer?: Array<string>
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
}

type Periode = {
	fom?: string
	tom?: string
}

export const sjekkManglerAaregData = (aaregData) => {
	return aaregData?.length < 1 || aaregData?.every((miljoData) => miljoData?.data?.length < 1)
}

const getHeader = (data: Arbeidsforhold) => {
	return `Arbeidsforhold (${data.arbeidsgiver.type}: ${
		data.arbeidsgiver.organisasjonsnummer || data.arbeidsgiver.offentligIdent
	})`
}

const Arbeidsforhold = ({ data }) => {
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
						{arbeidsforhold.ansettelsesperiode && (
							<TitleValue
								title="Arbeidsforhold type"
								value={arbeidsforhold.type}
								kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
							/>
						)}

						<TitleValue title="Arbeidsforhold-ID" value={arbeidsforhold.arbeidsforholdId} />

						{arbeidsforhold.ansettelsesperiode && arbeidsforhold.ansettelsesperiode.periode && (
							<TitleValue
								title="Ansatt fra"
								value={Formatters.formatDate(arbeidsforhold.ansettelsesperiode.periode.fom)}
							/>
						)}
						{arbeidsforhold.ansettelsesperiode && arbeidsforhold.ansettelsesperiode.periode && (
							<TitleValue
								title="Ansatt til"
								value={Formatters.formatDate(arbeidsforhold.ansettelsesperiode.periode.tom)}
							/>
						)}
						{/* //TODO: Sluttårsak mangler fra Aareg */}
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

export const AaregVisning = ({ liste, loading, bestilteMiljoer }: AaregVisningProps) => {
	if (loading) {
		return <Loading label="Laster Aareg-data" />
	}
	if (!liste) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerAaregData(liste)

	const forsteMiljo = liste.find((miljoData) => miljoData?.data?.length > 0)?.miljo

	return (
		<div>
			<SubOverskrift label="Arbeidsforhold" iconKind="arbeid" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ margin: '7px' }}>
					Kunne ikke hente arbeidsforhold-data på person
				</Alert>
			) : (
				<ErrorBoundary>
					<MiljoTabs bestilteMiljoer={bestilteMiljoer} forsteMiljo={forsteMiljo} data={liste}>
						<Arbeidsforhold />
						{/*<ArbeidsforholdMiljoeInfo ident={ident} bestilteMiljoer={bestilteMiljoer} />*/}
					</MiljoTabs>
				</ErrorBoundary>
			)}
		</div>
	)
}
