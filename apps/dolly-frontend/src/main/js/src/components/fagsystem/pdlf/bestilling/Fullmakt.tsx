import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { FullmaktOmraade, FullmaktValues } from '@/components/fagsystem/pdlf/PdlTypes'
import { arrayToString, formatDate, showLabel } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'
import { RelatertPerson } from '@/components/bestilling/sammendrag/visning/RelatertPerson'
import _get from 'lodash/get'
import { useFullmaktOmraader } from '@/utils/hooks/useFullmakt'

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
							<TitleValue
								title="Handling"
								value={arrayToString(
									omraade?.handling?.map((h: string) => showLabel('fullmaktHandling', h)),
								)}
							/>
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
								<EkspanderbarVisning vis={_get(fullmakt, 'nyFullmektig')} header={'FULLMEKTIG'}>
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
