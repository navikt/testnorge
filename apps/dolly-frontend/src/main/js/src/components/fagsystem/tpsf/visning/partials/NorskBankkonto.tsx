import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type Data = {
	data: NorskBankkontoData
}

type NorskBankkontoData = {
	kontonummer: string
}

export const Visning = ({ data }: Data) => (
	<>
		<div className="person-visning_content">
			<ErrorBoundary>
				<TitleValue title={'Kontonummer'} value={data.kontonummer} />
			</ErrorBoundary>
		</div>
	</>
)

export const NorskBankkonto = ({ data }: Data) => {
	if (!data) return null
	return (
		<div>
			<SubOverskrift label="Norsk bankkonto" iconKind="bankkonto" />
			<Visning data={data} />
		</div>
	)
}
