import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { SikkerhetstiltakData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type SikkerhetstiltakTypes = {
	sikkerhetstiltakListe: Array<SikkerhetstiltakData>
}

export const Sikkerhetstiltak = ({ sikkerhetstiltakListe }: SikkerhetstiltakTypes) => {
	if (!sikkerhetstiltakListe || sikkerhetstiltakListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Sikkerhetstiltak</BestillingTitle>
				<DollyFieldArray header="Sikkerhetstiltak" data={sikkerhetstiltakListe}>
					{(sikkerhetstiltak: SikkerhetstiltakData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Type sikkerhetstiltak"
									value={showLabel('sikkerhetstiltakType', sikkerhetstiltak.tiltakstype)}
								/>
								<TitleValue
									title="Kontaktperson"
									value={sikkerhetstiltak.kontaktperson.personident}
								/>
								<TitleValue title="Navkontor kode" value={sikkerhetstiltak.kontaktperson.enhet} />
								<TitleValue
									title="Gyldig f.o.m."
									value={formatDate(sikkerhetstiltak.gyldigFraOgMed)}
								/>
								<TitleValue
									title="Gyldig t.o.m."
									value={formatDate(sikkerhetstiltak.gyldigTilOgMed)}
								/>
								<TitleValue title="Master" value={sikkerhetstiltak.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
