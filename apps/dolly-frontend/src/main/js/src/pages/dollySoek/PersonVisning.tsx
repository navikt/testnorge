import StyledAlert from '@/components/ui/alert/StyledAlert'
import { NavigerTilPerson } from '@/pages/dollySoek/NavigerTilPerson'
import React from 'react'
import { PdlVisning } from '@/components/fagsystem/pdl/visning/PdlVisning'

export const PersonVisning = ({ person, loading }: any) => {
	const ident = person.hentIdenter?.identer?.find(
		(i) => i.gruppe !== 'AKTORID' && !i.historisk,
	)?.ident

	return (
		<>
			<PdlVisning pdlData={person} loading={loading} />
			<StyledAlert variant={'info'} size={'small'} inline style={{ marginTop: '20px' }}>
				Viser kun egenskaper fra PDL,{' '}
				<NavigerTilPerson ident={ident} linkTekst={'vis person i gruppe'} /> for å se egenskaper fra
				alle fagsystemer.
			</StyledAlert>
		</>
	)
}
