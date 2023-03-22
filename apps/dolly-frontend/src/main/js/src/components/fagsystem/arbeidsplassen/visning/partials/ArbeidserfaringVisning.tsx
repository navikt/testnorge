import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const ArbeidserfaringVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Arbeidserfaring" nested>
					{(arbeidserfaring) => (
						<>
							<TitleValue
								title="Stilling/yrke"
								value={Formatters.showLabel('jobbYrke', arbeidserfaring.styrkkode)}
							/>
							<TitleValue title="Alternativ tittel" value={arbeidserfaring.alternativeJobTitle} />
							<TitleValue title="Bedrift" value={arbeidserfaring.employer} />
							<TitleValue title="Sted" value={arbeidserfaring.location} />
							<TitleValue
								title="Arbeidsoppgaver"
								value={arbeidserfaring.description}
								size="medium"
							/>
							<TitleValue
								title="Ansatt fra"
								value={Formatters.formatDate(arbeidserfaring.fromDate)}
							/>
							<TitleValue
								title="Ansatt til"
								value={Formatters.formatDate(arbeidserfaring.toDate)}
							/>
							<TitleValue
								title="Nåværende jobb"
								value={Formatters.oversettBoolean(arbeidserfaring.ongoing)}
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
