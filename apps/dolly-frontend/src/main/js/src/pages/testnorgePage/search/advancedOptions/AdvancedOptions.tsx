import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import React from 'react'

type Props = {
	startOpen?: boolean
	content?: React.ReactNode
	children?: React.ReactNode
}

export const AdvancedOptions = ({ startOpen = false, content, children }: Props) => {
	const [visAvansert, setVisAvansert, setSkjulAvansert] = useBoolean(startOpen)
	const renderContent = children ? children : content

	return (
		<div style={{ marginBottom: '10px' }}>
			{visAvansert ? (
				<Button onClick={setSkjulAvansert} kind={'collapse'}>
					SKJUL AVANSERTE VALG
				</Button>
			) : (
				<Button onClick={setVisAvansert} kind={'expand'}>
					VIS AVANSERTE VALG
				</Button>
			)}
			{visAvansert && <div style={{ marginTop: '10px' }}>{renderContent}</div>}
		</div>
	)
}
