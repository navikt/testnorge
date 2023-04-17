import * as _ from 'lodash-es'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { codeToNorskLabel, formatDate } from '@/utils/DataFormatter'
import { Periode, Sykepenger } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

interface SykepengerVisning {
	data?: Sykepenger
}

export default ({ data }: SykepengerVisning) => {
	if (!data || _.isEmpty(data)) {
		return null
	}
	return (
		<>
			<h4>Sykepenger</h4>
			<div className="person-visning_content">
				<TitleValue title="Brutto utbetalt" value={data.bruttoUtbetalt} />
				<TitleValue
					title="Begrunnelse for reduksjon eller ikke utbetalt"
					value={codeToNorskLabel(data.begrunnelseForReduksjonEllerIkkeUtbetalt)}
				/>
				{data.arbeidsgiverperiodeListe && (
					<ErrorBoundary>
						<DollyFieldArray data={data.arbeidsgiverperiodeListe} header="Arbeidsgiverperioder">
							{(id: Periode) => (
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
