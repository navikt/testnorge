import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import React from 'react'

type EkspanderbarVisningTypes = {
	vis: any
	header: string
	children?: any
}

export const EkspanderbarVisning = ({ vis, header, children }: EkspanderbarVisningTypes) => {
	const [visPersonValg, setVisPersonValg, setSkjulPersonValg] = useBoolean(false)

	if (!vis) {
		return null
	}

	return (
		<div className="flexbox--full-width">
			<Button
				onClick={visPersonValg ? setSkjulPersonValg : setVisPersonValg}
				kind={visPersonValg ? 'chevron-up' : 'chevron-down'}
				style={visPersonValg ? { margin: '0 0 10px 0' } : { margin: '0' }}
			>
				{header}
			</Button>
			{visPersonValg && (
				<div className={'flexbox--align-start flexbox--wrap'} style={{ margin: '10px 0 0 0' }}>
					{children}
				</div>
			)}
		</div>
	)
}
