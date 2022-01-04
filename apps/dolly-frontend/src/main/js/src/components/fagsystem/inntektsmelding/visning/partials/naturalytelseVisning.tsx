import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Naturalytelse } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface NaturalytelseVisning {
	data?: Array<Naturalytelse>
	header: string
}

export default ({ data, header }: NaturalytelseVisning) => {
	if (!data || data.length < 1) return null
	return (
		<ErrorBoundary>
			<DollyFieldArray data={data} header={header} nested>
				{(id: Naturalytelse) => (
					<>
						<div className="person-visning_content">
							<TitleValue title="Beløp per måned" value={id.beloepPrMnd} />
							<TitleValue
								title="Naturalytelse type"
								value={Formatters.codeToNorskLabel(id.naturalytelseType)}
							/>
							<TitleValue title="Fra og med dato" value={Formatters.formatDate(id.fom)} />
						</div>
					</>
				)}
			</DollyFieldArray>
		</ErrorBoundary>
	)
}
