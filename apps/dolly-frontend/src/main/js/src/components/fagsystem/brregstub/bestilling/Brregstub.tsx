import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { arrayToString, formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { BrregTypes, Enhet, PersonRolle } from '@/components/fagsystem/brregstub/BrregTypes'
import { useBrregRoller, useBrregUnderstatuser } from '@/utils/hooks/useBrreg'

type BrregProps = {
	brregstub: BrregTypes
}

export const Brregstub = ({ brregstub }: BrregProps) => {
	if (!brregstub || isEmpty(brregstub)) {
		return null
	}

	const { understatuserOptions } = useBrregUnderstatuser()
	const { rollerOptions } = useBrregRoller()

	const understatusLabel = (understatus: number) =>
		understatuserOptions?.find((u) => u.value === understatus)?.label || understatus

	const rolleLabel = (rolle: string) =>
		rollerOptions?.find((r) => r.value === rolle)?.label || rolle

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Brønnøysundregistrene</BestillingTitle>
				<TitleValue
					title="Understatuser"
					value={arrayToString(brregstub.understatuser?.map((status) => understatusLabel(status)))}
				/>
				<DollyFieldArray header="Enhet" data={brregstub?.enheter}>
					{(enhet: Enhet, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Rolle" value={rolleLabel(enhet?.rolle)} />
								<TitleValue
									title="Registreringsdato"
									value={formatDate(enhet?.registreringsdato)}
								/>
								<TitleValue title="Organisasjonsnummer" value={enhet?.orgNr} />
								<TitleValue title="Foretaksnavn" value={enhet?.foretaksNavn?.navn1} />
								{enhet?.personroller?.length > 0 && (
									<DollyFieldArray header="Personroller" data={enhet?.personroller} nested>
										{(personrolle: PersonRolle, idy: number) => {
											return (
												<React.Fragment key={idy}>
													<TitleValue title="Egenskap" value={personrolle?.egenskap} />
													<TitleValue
														title="Har fratrådt"
														value={oversettBoolean(personrolle?.fratraadt)}
													/>
												</React.Fragment>
											)
										}}
									</DollyFieldArray>
								)}
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
