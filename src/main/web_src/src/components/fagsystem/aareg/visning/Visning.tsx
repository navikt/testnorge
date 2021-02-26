import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Arbeidsavtaler } from './partials/Arbeidsavtaler'
import { Arbeidsgiver } from './partials/Arbeidsgiver'
import { PermisjonPermitteringer } from './partials/PermisjonPermitteringer'
import { AntallTimerForTimeloennet } from './partials/AntallTimerForTimeloennet'
import { Utenlandsopphold } from './partials/Utenlandsopphold'
import { ArbeidKodeverk } from '~/config/kodeverk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type AaregVisning = {
	liste?: Array<Arbeidsforhold>
	loading?: boolean
}

type Arbeidsforhold = {
	type?: string
	ansettelsesperiode?: Ansettelsesperiode
	antallTimerForTimeloennet?: Array<unknown>
	arbeidsavtaler?: Array<unknown>
	arbeidsgiver?: Arbeidsgiver
	permisjonPermitteringer?: Array<unknown>
	utenlandsopphold?: Array<unknown>
	arbeidsforholdId?: string
}

type Arbeidsgiver = {
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

const getHeader = (data: Arbeidsforhold) => {
	return `Arbeidsforhold (${data.arbeidsgiver.type}: ${
		data.arbeidsgiver.organisasjonsnummer
			? data.arbeidsgiver.organisasjonsnummer
			: data.arbeidsgiver.offentligIdent
	})`
}

export const AaregVisning = ({ liste, loading }: AaregVisning) => {
	if (loading) return <Loading label="Laster Aareg-data" />
	if (!liste) return null

	const sortedData = liste
		.slice()
		.sort((a, b) => parseInt(a.arbeidsforholdId) - parseInt(b.arbeidsforholdId))

	return (
		<div>
			<SubOverskrift label="Arbeidsforhold" iconKind="arbeid" />
			<ErrorBoundary>
				<DollyFieldArray
					header="Arbeidsforhold"
					getHeader={getHeader}
					data={sortedData}
					expandable={sortedData.length > 1}
				>
					{(arbeidsforhold: Arbeidsforhold) => (
						<React.Fragment>
							<div className="person-visning_content">
								{arbeidsforhold.ansettelsesperiode && (
									<TitleValue
										title="Arbeidsforhold type"
										value={arbeidsforhold.type}
										kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
									/>
								)}

								{arbeidsforhold.ansettelsesperiode && arbeidsforhold.ansettelsesperiode.periode && (
									<TitleValue
										title="Startdato"
										value={Formatters.formatStringDates(
											arbeidsforhold.ansettelsesperiode.periode.fom
										)}
									/>
								)}
								{arbeidsforhold.ansettelsesperiode && arbeidsforhold.ansettelsesperiode.periode && (
									<TitleValue
										title="Sluttdato"
										value={Formatters.formatStringDates(
											arbeidsforhold.ansettelsesperiode.periode.tom
										)}
									/>
								)}
							</div>

							<AntallTimerForTimeloennet data={arbeidsforhold.antallTimerForTimeloennet} />

							<Arbeidsavtaler data={arbeidsforhold.arbeidsavtaler} />

							<Arbeidsgiver data={arbeidsforhold.arbeidsgiver} />

							<PermisjonPermitteringer data={arbeidsforhold.permisjonPermitteringer} />

							<Utenlandsopphold data={arbeidsforhold.utenlandsopphold} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
