import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { Kurs } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type KursVisningProps = {
	data?: Array<Kurs>
}

export const KursVisning = ({ data }: KursVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Kurs" nested>
					{(kurs: Kurs) => (
						<>
							<TitleValue title="Kursnavn" value={kurs.title} />
							<TitleValue title="Kursholder" value={kurs.issuer} />
							<TitleValue title="Fullført" value={formatDate(kurs.date)} />
							<TitleValue title="Kurslengde" value={showLabel('kursLengde', kurs.durationUnit)} />
							<TitleValue
								title={`Antall ${
									kurs.durationUnit && kurs.durationUnit !== 'UKJENT'
										? showLabel('kursLengde', kurs.durationUnit)
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
