import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { AnnenErfaring } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type AnnenErfaringVisningProps = {
	data?: Array<AnnenErfaring>
}

export const AnnenErfaringVisning = ({ data }: AnnenErfaringVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Andre erfaringer" nested>
					{(annenErfaring: AnnenErfaring) => (
						<>
							<TitleValue title="Rolle" value={annenErfaring.role} />
							<TitleValue title="Beskrivelse" value={annenErfaring.description} size="medium" />
							<TitleValue title="Startdato" value={formatDate(annenErfaring.fromDate)} />
							<TitleValue title="Sluttdato" value={formatDate(annenErfaring.toDate)} />
							<TitleValue title="Pågående" value={oversettBoolean(annenErfaring.ongoing)} />
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
