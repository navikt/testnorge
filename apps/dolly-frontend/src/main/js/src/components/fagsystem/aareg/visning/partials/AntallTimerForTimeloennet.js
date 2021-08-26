import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const AntallTimerForTimeloennet = ({ data }) => {
	if (!data || data.length === 0) return null

	return (
		<React.Fragment>
			<h4>Antall timer for timelønnet</h4>
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Antall timer" value={id.antallTimer} />
							{id.periode && (
								<TitleValue
									title="Periode fra"
									value={Formatters.formatStringDates(id.periode.fom)}
								/>
							)}
							{id.periode && (
								<TitleValue
									title="Periode til"
									value={Formatters.formatStringDates(id.periode.tom)}
								/>
							)}
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</React.Fragment>
	)
}
