import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { Arbeidserfaring } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type ArbeidserfaringVisningProps = {
	data?: Array<Arbeidserfaring>
}

export const ArbeidserfaringVisning = ({ data }: ArbeidserfaringVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Arbeidserfaring" nested>
					{(arbeidserfaring: Arbeidserfaring) => (
						<>
							<TitleValue
								title="Stilling/yrke"
								value={showLabel('jobbYrke', arbeidserfaring.styrkkode)}
							/>
							<TitleValue title="Alternativ tittel" value={arbeidserfaring.alternativeJobTitle} />
							<TitleValue title="Bedrift" value={arbeidserfaring.employer} />
							<TitleValue title="Sted" value={arbeidserfaring.location} />
							<TitleValue
								title="Arbeidsoppgaver"
								value={arbeidserfaring.description}
								size="medium"
							/>
							<TitleValue title="Ansatt fra" value={formatDate(arbeidserfaring.fromDate)} />
							<TitleValue title="Ansatt til" value={formatDate(arbeidserfaring.toDate)} />
							<TitleValue title="Nåværende jobb" value={oversettBoolean(arbeidserfaring.ongoing)} />
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
