import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import {
	Refusjon,
	EndringIRefusjon
} from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface RefusjonVisning {
	data?: Refusjon
}

export default ({ data }: RefusjonVisning) => {
	if (!data || _isEmpty(data)) return null
	return (
		<>
			<h4>Refusjon</h4>
			<div className="person-visning_content">
				<TitleValue title="Refusjonsbeløp per måned" value={data.refusjonsbeloepPrMnd} />
				<TitleValue title="Opphørsdato" value={Formatters.formatDate(data.refusjonsopphoersdato)} />
				{data.endringIRefusjonListe && (
					<ErrorBoundary>
						<DollyFieldArray data={data.endringIRefusjonListe} header="Endring i refusjon">
							{(id: EndringIRefusjon) => (
								<div className="person-visning_content">
									<TitleValue title="Ednringsdato" value={Formatters.formatDate(id.endringsdato)} />
									<TitleValue title="Refusjonsbeløp per måned" value={id.refusjonsbeloepPrMnd} />
								</div>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				)}
			</div>
		</>
	)
}
