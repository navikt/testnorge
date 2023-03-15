import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const KursVisning = ({ data }) => {
	if (data?.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Kurs" nested>
					{(kurs, idx: number) => (
						<>
							<TitleValue title="Kursnavn" value={kurs.title} />
							<TitleValue title="Kursholder" value={kurs.issuer} />
							<TitleValue title="Fullført" value={Formatters.formatDate(kurs.date)} />
							<TitleValue
								title="Kurslengde"
								value={Formatters.showLabel('kursLengde', kurs.durationUnit)}
							/>
							<TitleValue
								title={`Antall ${
									kurs.durationUnit && kurs.durationUnit !== 'UKJENT'
										? Formatters.showLabel('kursLengde', kurs.durationUnit)
										: ''
								}`}
								value={kurs.duration}
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
