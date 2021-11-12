import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Diagnose } from '~/components/fagsystem/sykdom/SykemeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type BidiagnoserProps = {
	data: Array<Diagnose>
}

export const Bidiagnoser = ({ data }: BidiagnoserProps) => {
	if (!data || data.length === 0) return null
	return (
		<>
			<h4>Bidiagnoser</h4>
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(bidiagnose: Diagnose, idx: string) => (
						<div key={idx} className="person-visning_content">
							<TitleValue title="Diagnose" value={bidiagnose.diagnose} />
							<TitleValue title="Diagnosekode" value={bidiagnose.diagnosekode} />
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</>
	)
}
