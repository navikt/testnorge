import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { VergeEllerFullmektig } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type VergeProps = {
	data: VergeEllerFullmektig
	type: string
}

export const Verge = ({ data, type }: VergeProps) => {
	if (!data) {
		return null
	}
	const harFullmektig = type.includes('fullmakt')

	return (
		<>
			<div className="person-visning_content">
				<h4 style={{ width: '100%' }}>{harFullmektig ? 'Fullmektig' : 'Verge'}</h4>
				<TitleValue title="Ident" value={data.motpartsPersonident} visKopier />
				<TitleValue title="Fornavn" value={data.navn?.fornavn} />
				<TitleValue title="Mellomnavn" value={data.navn?.mellomnavn} />
				<TitleValue title="Etternavn" value={data.navn?.etternavn} />
			</div>
		</>
	)
}
