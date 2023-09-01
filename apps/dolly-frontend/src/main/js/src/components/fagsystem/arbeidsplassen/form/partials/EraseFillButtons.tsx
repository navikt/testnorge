import Button from '@/components/ui/button/Button'
import * as React from 'react'

export const EraseFillButtons = ({ formikBag, path, initialErase, initialFill }) => {
	return (
		<div className="flexbox--full-width">
			<div className="flexbox--flex-wrap">
				<Button
					kind="designsystem-eraser"
					onClick={() => formikBag.setFieldValue(path, initialErase)}
					style={{ position: 'initial' }}
				>
					TØM SKJEMA
				</Button>
				<Button
					kind="designsystem-edit"
					onClick={() => formikBag.setFieldValue(path, initialFill)}
					style={{ position: 'initial', marginLeft: '7px' }}
				>
					FYLL SKJEMA
				</Button>
			</div>
		</div>
	)
}
