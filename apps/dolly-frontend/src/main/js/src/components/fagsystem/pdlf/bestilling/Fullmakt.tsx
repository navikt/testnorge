import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { FullmaktOmraade, FullmaktValues } from '@/components/fagsystem/pdlf/PdlTypes'
import { formatDate } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/partials/EkspanderbarVisning'
import { RelatertPerson } from '@/components/bestilling/sammendrag/partials/RelatertPerson'
import * as _ from 'lodash-es'
import { useFullmaktOmraader } from '@/utils/hooks/useFullmakt'
import { handlingLabel } from '@/components/fagsystem/fullmakt/visning/Fullmakt'

type FullmaktTypes = {
	fullmaktListe: Array<FullmaktValues>
}

export const Fullmakt = ({ fullmaktListe }: FullmaktTypes) => {
	const { omraadeKodeverk } = useFullmaktOmraader()

	if (!fullmaktListe || fullmaktListe.length < 1) {
		return null
	}

	const Omraader = ({ omraader }: { omraader: Array<FullmaktOmraade> | undefined }) => {
		if (!omraader || omraader.length < 1) {
			return null
		}
		return (
			<DollyFieldArray header="Områder" data={omraader} nested>
				{(omraade: any, idx: number) => {
					const temaLabel = omraadeKodeverk?.find((tema) => tema.value === omraade?.tema)?.label

					return (
						<React.Fragment key={idx}>
							<TitleValue title="Tema" value={temaLabel} />
							<TitleValue title="Handling" value={handlingLabel(omraade?.handling)} />
						</React.Fragment>
					)
				}}
			</DollyFieldArray>
		)
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Fullmakt</BestillingTitle>
				<DollyFieldArray header="Fullmakt" data={fullmaktListe}>
					{(fullmakt: FullmaktValues, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<Omraader omraader={fullmakt.omraade} />
								<TitleValue title="Gyldig f.o.m." value={formatDate(fullmakt.gyldigFraOgMed)} />
								<TitleValue title="Gyldig t.o.m." value={formatDate(fullmakt.gyldigTilOgMed)} />
								<TitleValue
									title="Fullmektig"
									value={
										fullmakt.fullmektig
											? `${fullmakt.fullmektig} - ${fullmakt.fullmektigsNavn}`
											: null
									}
								/>
								<EkspanderbarVisning vis={_.get(fullmakt, 'nyFullmektig')} header={'FULLMEKTIG'}>
									<RelatertPerson personData={fullmakt.nyFullmektig} tittel="Fullmektig" />
								</EkspanderbarVisning>
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
