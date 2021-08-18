import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import {
	Omsorgspenger,
	DelvisFravaer,
	Fravaer
} from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface OmsorgspengerVisning {
	data?: Omsorgspenger
}

export default ({ data }: OmsorgspengerVisning) => {
	if (!data) return null

	return (
		<>
			<h4>Omsorgspenger</h4>
			<div className="person-visning_content">
				<TitleValue
					title="Har utbetalt pliktige dager"
					value={Formatters.oversettBoolean(data.harUtbetaltPliktigeDager)}
				/>
				{data.delvisFravaersListe && (
					<ErrorBoundary>
						<DollyFieldArray data={data.delvisFravaersListe} header="Delvis fravær">
							{(id: DelvisFravaer) => (
								<>
									<div className="person-visning_content">
										<TitleValue title="Dato" value={Formatters.formatDate(id.dato)} />
										<TitleValue title="Timer" value={id.timer} />
									</div>
								</>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				)}

				{data.fravaersPerioder && (
					<ErrorBoundary>
						<DollyFieldArray data={data.fravaersPerioder} header="Fraværsperioder">
							{(id: Fravaer) => (
								<>
									<div className="person-visning_content">
										<TitleValue title="Fra og med dato" value={Formatters.formatDate(id.fom)} />
										<TitleValue title="Til og med dato" value={Formatters.formatDate(id.tom)} />
									</div>
								</>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				)}
			</div>
		</>
	)
}
