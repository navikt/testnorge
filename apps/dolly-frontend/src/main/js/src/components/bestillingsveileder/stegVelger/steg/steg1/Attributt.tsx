import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { CheckboxGroup } from '@navikt/ds-react'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

export const Attributt = ({
	attr,
	vis = true,
	disabled = false,
	title = null,
	id = null,
	infoTekst = '',
	...props
}) => {
	if (!vis) {
		return null
	}
	return (
		<div title={title} style={{ display: 'flex' }}>
			<DollyCheckbox
				wrapperSize={null}
				label={attr?.label}
				attributtCheckbox={true}
				size={'small'}
				onChange={attr?.checked ? attr?.remove : attr?.add}
				value={attr?.label}
				isDisabled={disabled}
				id={id}
				{...props}
			/>
			{infoTekst && (
				<Hjelpetekst style={{ marginLeft: '-25px', marginTop: '3px' }}>{infoTekst}</Hjelpetekst>
			)}
		</div>
	)
}

export const AttributtKategori = ({ title, children, attr }) => {
	const values = attr && Object.values(attr)
	const checked = values
		?.filter((attribute) => attribute.checked)
		?.map((attribute) => attribute.label)

	const attributter = Array.isArray(children) ? children : [children]
	const attributterSomSkalVises = attributter.some(
		(attr) => attr.props.vis || !attr.props.hasOwnProperty('vis'),
	)
	if (!attributterSomSkalVises) {
		return null
	}
	return (
		<CheckboxGroup name={title} legend={title} value={checked}>
			<div className="attributt-velger_panelsubcontent">{children}</div>
		</CheckboxGroup>
	)
}
