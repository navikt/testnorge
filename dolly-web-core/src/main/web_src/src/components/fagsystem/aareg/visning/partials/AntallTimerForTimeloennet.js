import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const AntallTimerForTimeloennet = ({ data }) => {
	if (!data || data.length === 0) return false

	return (
		<div>
			<h4>Antall timer for timelønnet</h4>
			<div className="person-visning_content">
				{data.map((id, idx) => (
					<div key={idx}>
						<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
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
				))}
			</div>
		</div>
	)
}
