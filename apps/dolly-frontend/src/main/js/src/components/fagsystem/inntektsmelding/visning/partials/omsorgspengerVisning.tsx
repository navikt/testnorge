import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import {
	DelvisFravaer,
	Fravaer,
	Omsorgspenger,
} from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

interface OmsorgspengerVisning {
	data?: Omsorgspenger
}

export default ({ data }: OmsorgspengerVisning) => {
	if (!data) {
		return null
	}

	return (
		<>
			<h4>Omsorgspenger</h4>
			<div className="person-visning_content">
				<TitleValue
					title="Har utbetalt pliktige dager"
					value={oversettBoolean(data.harUtbetaltPliktigeDager)}
				/>
				{data.delvisFravaersListe && (
					<ErrorBoundary>
						<DollyFieldArray data={data.delvisFravaersListe} header="Delvis fravær">
							{(id: DelvisFravaer) => (
								<>
									<div className="person-visning_content">
										<TitleValue title="Dato" value={formatDate(id.dato)} />
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
										<TitleValue title="Fra og med dato" value={formatDate(id.fom)} />
										<TitleValue title="Til og med dato" value={formatDate(id.tom)} />
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
