import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { InstTypes } from '@/components/fagsystem/inst/InstTypes'

type InstProps = {
	inst: Array<InstTypes>
}

export const Inst = ({ inst }: InstProps) => {
	if (!inst || inst.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Institusjonsopphold</BestillingTitle>
				<DollyFieldArray header="Opphold" data={inst}>
					{(opphold: InstTypes, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Institusjonstype"
									value={showLabel('institusjonstype', opphold.institusjonstype)}
								/>
								<TitleValue
									title="Varighet"
									value={opphold.varighet && showLabel('varighet', opphold.varighet)}
								/>
								<TitleValue title="Startdato" value={formatDate(opphold.startdato)} />
								<TitleValue
									title="Forventet sluttdato"
									value={formatDate(opphold.forventetSluttdato)}
								/>
								<TitleValue title="Sluttdato" value={formatDate(opphold.sluttdato)} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
