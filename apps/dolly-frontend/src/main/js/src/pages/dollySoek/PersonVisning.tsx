import { NavigerTilPerson } from '@/pages/dollySoek/NavigerTilPerson'
import React from 'react'
import { PdlVisning } from '@/components/fagsystem/pdl/visning/PdlVisning'
import { InlineMessage } from '@navikt/ds-react'

export const PersonVisning = ({ person, loading }: any) => {
	const ident = person.hentIdenter?.identer?.find(
		(i) => i.gruppe !== 'AKTORID' && !i.historisk,
	)?.ident

	return (
		<>
			<PdlVisning pdlData={person} loading={loading} />
			<InlineMessage
				status="info"
				size={'small'}
				style={{ marginBottom: '20px', marginTop: '20px' }}
			>
				Viser kun egenskaper fra PDL,{' '}
				<NavigerTilPerson ident={ident} linkTekst={'vis person i gruppe'} /> for å se egenskaper fra
				alle fagsystemer.
			</InlineMessage>
		</>
	)
}
