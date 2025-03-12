// import { useBestillingerPaaIdent } from '@/utils/hooks/usePersonSoek'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { BestillingVisningListe } from '@/pages/dollySoek/BestillingVisningModal'
import PdlfVisningConnector from '@/components/fagsystem/pdlf/visning/PdlfVisningConnector'
import { NavigerTilPerson } from '@/pages/dollySoek/NavigerTilPerson'
import React from 'react'
import PdlVisningConnector from '@/components/fagsystem/pdl/visning/PdlVisningConnector'

export const PersonVisning = ({ person, loading }: any) => {
	// console.log('person: ', person) //TODO - SLETT MEG
	// const ident = person.person?.ident || person.ident
	const ident = person.hentIdenter?.identer?.find(
		(i) => i.gruppe === 'FOLKEREGISTERIDENT' && !i.historisk,
	)?.ident
	// const { bestillinger } = useBestillingerPaaIdent(ident)

	return (
		<>
			{/*{bestillinger?.data?.length > 0 && (*/}
			{/*	<StyledAlert variant={'info'} size={'small'} style={{ marginTop: '10px' }}>*/}
			{/*		Søket er gjort mot bestillinger foretatt i Dolly, og denne personen ble returnert fordi én*/}
			{/*		eller flere av bestillingene samsvarer med søket. Fordi det er mulig å gjøre endringer på*/}
			{/*		personer i etterkant av bestilling er det ikke sikkert at personen stemmer overens med*/}
			{/*		alle søkekriteriene. Se bestillingen(e) knyttet til personen her:*/}
			{/*		<BestillingVisningListe bestillinger={bestillinger?.data} />*/}
			{/*	</StyledAlert>*/}
			{/*)}*/}
			{/*<PdlfVisningConnector*/}
			{/*	fagsystemData={{ pdlforvalter: { person: person.hentPerson } }}*/}
			{/*	loading={loading}*/}
			{/*	erRedigerbar={false}*/}
			{/*/>*/}
			<PdlVisningConnector pdlData={person} loading={loading} />
			<StyledAlert variant={'info'} size={'small'} inline style={{ marginTop: '20px' }}>
				Viser kun egenskaper fra PDL,{' '}
				<NavigerTilPerson ident={ident} linkTekst={'vis person i gruppe'} /> for å se egenskaper fra
				alle fagsystemer.
			</StyledAlert>
		</>
	)
}
