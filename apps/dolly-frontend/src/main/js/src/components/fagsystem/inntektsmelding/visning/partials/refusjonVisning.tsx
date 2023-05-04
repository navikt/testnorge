import * as _ from 'lodash-es'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import {
	EndringIRefusjon,
	Refusjon,
} from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

interface RefusjonVisning {
	data?: Refusjon
}

export default ({ data }: RefusjonVisning) => {
	if (!data || _.isEmpty(data)) {
		return null
	}
	return (
		<>
			<h4>Refusjon</h4>
			<div className="person-visning_content">
				<TitleValue title="Refusjonsbeløp per måned" value={data.refusjonsbeloepPrMnd} />
				<TitleValue title="Opphørsdato" value={formatDate(data.refusjonsopphoersdato)} />
				{data.endringIRefusjonListe && (
					<ErrorBoundary>
						<DollyFieldArray data={data.endringIRefusjonListe} header="Endring i refusjon">
							{(id: EndringIRefusjon) => (
								<div className="person-visning_content">
									<TitleValue title="Ednringsdato" value={formatDate(id.endringsdato)} />
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
