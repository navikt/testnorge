import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Checkbox, CheckboxGroup } from '@navikt/ds-react'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import React, { useCallback, useEffect, useRef, useState } from 'react'

interface AttributtProps {
	attr: {
		label: string
		checked: boolean
		add: () => void
		remove: () => void
		[key: string]: any
	}
	label?: string
	vis?: boolean
	disabled?: boolean
	infoTekst?: string
}

export const Attributt = ({ attr, vis = true, ...props }: AttributtProps) => {
	if (!vis) {
		return null
	}

	return (
		<DollyCheckbox
			attributtCheckbox
			value={attr.label}
			label={attr.label}
			size="small"
			{...props}
		/>
	)
}

interface AttributtKategoriProps {
	title: string
	children: React.ReactNode
	attr: Record<string, AttributtProps['attr']>
}

export const AttributtKategori = ({ title, children, attr }: AttributtKategoriProps) => {
	// Store attribute values
	const attrValues = attr ? Object.values(attr) : []

	// Track checkbox states
	const [checkedValues, setCheckedValues] = useState<string[]>([])

	// Store original methods to break circular references
	const origMethods = useRef<Map<string, { add: () => void; remove: () => void }>>(new Map())

	// Calculate current checked states from attributes
	const getCheckedValues = useCallback(() => {
		if (!attr) return []
		return Object.values(attr)
			.filter((v) => v.checked)
			.map((v) => v.label)
	}, [attr])

	// Update UI state without triggering infinite loops
	const updateCheckedState = useCallback(() => {
		setCheckedValues(getCheckedValues())
	}, [getCheckedValues])

	// Initialize component once - store original methods before replacing
	useEffect(() => {
		if (!attr) return

		// Save original methods
		Object.values(attr).forEach((value) => {
			if (!origMethods.current.has(value.label)) {
				origMethods.current.set(value.label, {
					add: value.add,
					remove: value.remove,
				})
			}
		})

		// Replace with safe wrappers
		Object.values(attr).forEach((value) => {
			const original = origMethods.current.get(value.label)
			if (!original) return

			value.add = function safeAdd() {
				original.add()
				setTimeout(updateCheckedState, 0)
			}

			value.remove = function safeRemove() {
				original.remove()
				setTimeout(updateCheckedState, 0)
			}
		})

		// Initialize checked values
		updateCheckedState()

		// Clean up on unmount
		return () => {
			Object.values(attr).forEach((value) => {
				const original = origMethods.current.get(value.label)
				if (original) {
					value.add = original.add
					value.remove = original.remove
				}
			})
		}
	}, [attr, updateCheckedState])

	// Handle checkbox change
	const handleChange = (selectedLabels: string[]) => {
		// Determine what changed
		const newlySelected = selectedLabels.filter((label) => !checkedValues.includes(label))

		const newlyDeselected = checkedValues.filter((label) => !selectedLabels.includes(label))

		// Process changes using original methods to avoid infinite recursion
		newlySelected.forEach((label) => {
			const item = attrValues.find((v) => v.label === label)
			if (item) {
				const original = origMethods.current.get(label)
				if (original && original.add) {
					original.add()
				}
			}
		})

		newlyDeselected.forEach((label) => {
			const item = attrValues.find((v) => v.label === label)
			if (item) {
				const original = origMethods.current.get(label)
				if (original && original.remove) {
					original.remove()
				}
			}
		})

		// Update state after operations
		updateCheckedState()
	}

	// Filter visible attributes
	const attributter = React.Children.toArray(children) as React.ReactElement<AttributtProps>[]
	const visibleAttributter = attributter.filter((attributt) => attributt.props.vis !== false)

	if (visibleAttributter.length < 1) {
		return null
	}

	return (
		<CheckboxGroup legend={title} value={checkedValues} onChange={handleChange}>
			<div className="attributt-velger_panelsubcontent">
				{visibleAttributter.map((attributt, i) => {
					const { infoTekst } = attributt.props
					return (
						<div className="attributt-velger_panel-content" key={i}>
							{attributt}
							{infoTekst && <Hjelpetekst>{infoTekst}</Hjelpetekst>}
						</div>
					)
				})}
			</div>
		</CheckboxGroup>
	)
}
