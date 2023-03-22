import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const AnnenErfaringVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Andre erfaringer" nested>
					{(annenErfaring) => (
						<>
							<TitleValue title="Rolle" value={annenErfaring.role} />
							<TitleValue title="Beskrivelse" value={annenErfaring.description} size="medium" />
							<TitleValue title="Startdato" value={Formatters.formatDate(annenErfaring.fromDate)} />
							<TitleValue title="Sluttdato" value={Formatters.formatDate(annenErfaring.toDate)} />
							<TitleValue
								title="Pågående"
								value={Formatters.oversettBoolean(annenErfaring.ongoing)}
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
