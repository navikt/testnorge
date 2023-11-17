import Button from '@/components/ui/button/Button'
import * as React from 'react'

export const EraseFillButtons = ({ formMethods, path, initialErase, initialFill }) => {
	return (
		<div className="flexbox--full-width">
			<div className="flexbox--flex-wrap">
				<Button
					kind="eraser"
					onClick={() => formMethods.setValue(path, initialErase)}
					style={{ position: 'initial' }}
				>
					TØM SKJEMA
				</Button>
				<Button
					kind="edit"
					onClick={() => formMethods.setValue(path, initialFill)}
					style={{ position: 'initial', marginLeft: '7px' }}
				>
					FYLL SKJEMA
				</Button>
			</div>
		</div>
	)
}
