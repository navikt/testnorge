import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Sykepenger, Periode } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface SykepengerVisning {
	data?: Sykepenger
}

export default ({ data }: SykepengerVisning) => {
	if (!data || _isEmpty(data)) return null
	return (
		<>
			<h4>Sykepenger</h4>
			<div className="person-visning_content">
				<TitleValue title="Brutto utbetalt" value={data.bruttoUtbetalt} />
				<TitleValue
					title="Begrunnelse for reduksjon eller ikke utbetalt"
					value={Formatters.codeToNorskLabel(data.begrunnelseForReduksjonEllerIkkeUtbetalt)}
				/>
				{data.arbeidsgiverperiodeListe && (
					<ErrorBoundary>
						<DollyFieldArray data={data.arbeidsgiverperiodeListe} header="Arbeidsgiverperioder">
							{(id: Periode) => (
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
