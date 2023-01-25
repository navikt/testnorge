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
import Formatters from '@/utils/DataFormatter'
import React from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import Bestillingskriterier from '@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'
import { AmeldingBestilling } from '@/components/fagsystem/aareg/visning/partials/AmeldingBestilling'

type AaregVisningProps = {
	ident?: string
	liste?: Array<MiljoDataListe>
	loading?: boolean
	bestillingIdListe?: Array<string>
}

type MiljoDataListe = {
	miljo: string
	data: Array<Arbeidsforhold>
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

const getHeader = (data: Arbeidsforhold) => {
	return `Arbeidsforhold (${data.arbeidsgiver.type}: ${
		data.arbeidsgiver.organisasjonsnummer || data.arbeidsgiver.offentligIdent
	})`
}

const Arbeidsforhold = ({ data }: ArbeidsforholdArray) => {
	if (!data) return null
	// console.log('amelding: ', amelding) //TODO - SLETT MEG

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
										value={Formatters.formatDate(arbeidsforhold.ansettelsesperiode.periode.fom)}
									/>
								)}
								{arbeidsforhold.ansettelsesperiode.periode && (
									<TitleValue
										title="Ansatt til"
										value={Formatters.formatDate(arbeidsforhold.ansettelsesperiode.periode.tom)}
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
	loading,
	bestillingListe,
	bestillingIdListe,
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
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo =
		liste?.find((miljoData) => miljoData?.data?.length > 0)?.miljo || liste?.[0]?.miljo

	// Faar ikke hente tilbake a-meldinger, viser derfor bestillingsdata
	const amelding = bestillingListe
		.map((bestilling) => bestilling?.data?.aareg?.[0]?.amelding)
		?.filter((amelding) => amelding)
	const harAmeldingBestilling = amelding?.some((bestilling) => bestilling?.length > 0)
	console.log('amelding: ', amelding) //TODO - SLETT MEG

	return (
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
						data={liste}
					>
						{/*<Arbeidsforhold amelding={amelding} />*/}
						<Arbeidsforhold />
					</MiljoTabs>
					{harAmeldingBestilling && <AmeldingBestilling bestillinger={amelding} />}
				</ErrorBoundary>
			)}
		</div>
	)
}
