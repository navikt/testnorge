import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Periode } from '~/components/fagsystem/sykdom/SykemeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type PerioderProps = {
	data: Array<Periode>
}

export const Perioder = ({ data }: PerioderProps) => {
	if (!data || data.length === 0) return null
	return (
		<>
			<h4>Perioder</h4>
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(periode: Periode, idx: string) => (
						<div key={idx} className="person-visning_content">
							<TitleValue title="F.o.m. dato" value={Formatters.formatStringDates(periode.fom)} />
							<TitleValue title="T.o.m. dato" value={Formatters.formatStringDates(periode.tom)} />
							<TitleValue title="Aktivitet" value={periode.aktivitet.aktivitet} />
							<TitleValue
								title="Antall behandlingsdager"
								value={periode.aktivitet.behandlingsdager}
							/>
							<TitleValue title="Grad" value={periode.aktivitet.grad} />
							<TitleValue
								title="Har reisetilskudd"
								value={Formatters.oversettBoolean(periode.aktivitet.reisetilskudd)}
							/>
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</>
	)
}
