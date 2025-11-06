import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { codeToNorskLabel, formatDate, formatDateTime, showLabel } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import {
	YrkesskadePeriodeTypes,
	YrkesskaderTypes,
	YrkesskadeTypes,
} from '@/components/fagsystem/yrkesskader/YrkesskaderTypes'

export const Yrkesskader = ({ yrkesskader }: YrkesskaderTypes) => {
	if (!yrkesskader || yrkesskader.length === 0) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Yrkesskader</BestillingTitle>
				<DollyFieldArray header="Yrkesskade" data={yrkesskader}>
					{(yrkesskade: YrkesskadeTypes, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Rolletype" value={codeToNorskLabel(yrkesskade.rolletype)} />
								<TitleValue
									title="Innmelderrolle"
									value={codeToNorskLabel(yrkesskade.innmelderrolle)}
								/>
								<TitleValue
									title="Klassifisering"
									value={showLabel('klassifisering', yrkesskade.klassifisering)}
								/>
								<TitleValue title="Referanse" value={yrkesskade.referanse} />
								<TitleValue
									title="Ferdigstill sak"
									value={showLabel('ferdigstillSak', yrkesskade.ferdigstillSak)}
								/>
								<TitleValue title="Tidstype" value={showLabel('tidstype', yrkesskade.tidstype)} />
								<TitleValue
									title="Skadetidspunkt"
									value={formatDateTime(yrkesskade.skadetidspunkt)}
								/>
								{yrkesskade.perioder?.length > 0 && (
									<DollyFieldArray header="Perioder" data={yrkesskade.perioder} nested>
										{(periode: YrkesskadePeriodeTypes, idy: number) => {
											return (
												<React.Fragment key={idy}>
													<TitleValue title="Fra dato" value={formatDate(periode.fra)} />
													<TitleValue title="Til dato" value={formatDate(periode.til)} />
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
