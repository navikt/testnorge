import { NyIdent } from '@/components/fagsystem/pdlf/PdlTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

type NyIdentitetTypes = {
	nyIdentitetListe: Array<NyIdent>
}

export const NyIdentitet = ({ nyIdentitetListe }: NyIdentitetTypes) => {
	if (!nyIdentitetListe || nyIdentitetListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Ny identitet</BestillingTitle>
				<DollyFieldArray header="Ny identitet" data={nyIdentitetListe}>
					{(nyIdentitet: NyIdent, idx: number) => {
						const erEksisterende = !!(
							nyIdentitet.eksisterendePerson || nyIdentitet.eksisterendeIdent
						)
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Ident fra"
									value={erEksisterende ? 'Eksisterende person' : 'Ny ident'}
								/>
								<TitleValue title="Eksisterende ident" value={nyIdentitet.eksisterendeIdent} />
								<TitleValue
									title="Identtype"
									value={nyIdentitet.identtype || (!erEksisterende && 'Samme som gjeldende')}
								/>
								<TitleValue title="Syntetisk" value={nyIdentitet.syntetisk && 'Ja'} />
								<TitleValue title="ID2032" value={nyIdentitet.id2032 && 'Ja'} />
								<TitleValue title="Master" value={nyIdentitet.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
