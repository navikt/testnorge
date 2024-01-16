import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import React from 'react'
import { RelatertPerson } from '@/components/bestilling/sammendrag/visning/RelatertPerson'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'

type EkspanderbarVisningTypes = {
	data: any
	header: string
}
export const EkspanderbarVisning = ({ data, header }: EkspanderbarVisningTypes) => {
	const [visPersonValg, setVisPersonValg, setSkjulPersonValg] = useBoolean(false)

	if (!data || isEmpty(data, ['syntetisk'])) {
		return null
	}

	return (
		<div className="flexbox--full-width">
			<Button
				onClick={visPersonValg ? setSkjulPersonValg : setVisPersonValg}
				kind={visPersonValg ? 'chevron-up' : 'chevron-down'}
				style={visPersonValg ? { margin: '10px 0 10px 0' } : { margin: '10px 0 0 0' }}
			>
				{header}
			</Button>
			{visPersonValg && (
				<div className={'flexbox--align-start flexbox--wrap'} style={{ margin: '10px 0 10px 0' }}>
					<RelatertPerson personData={data} />
				</div>
			)}
		</div>
	)
}
