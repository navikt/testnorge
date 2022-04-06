import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import { NorskBankkonto, UtenlandskBankkonto } from '~/components/fagsystem/tpsf/visning/partials'

type PdlBankkontoProps = {
	data: any
	loading?: boolean
}

export const PdlBankkonto = ({ data, loading = false }: PdlBankkontoProps) => {
	if (loading) return <Loading />
	if (!data) return null

	return (
		<>
			<UtenlandskBankkonto data={data.bankkontonrUtland} />
			<NorskBankkonto data={data.bankkontonrNorsk} />
		</>
	)
}
