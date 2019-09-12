import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Select from 'react-virtualized-select'
import cn from 'classnames'
import _get from 'lodash/get'
import './Select.less'

export default class DollySelect extends PureComponent {
	static propTypes = {
		name: PropTypes.string.isRequired,
		label: PropTypes.string.isRequired,
		placeholder: PropTypes.string
	}

	static defaultProps = {
		placeholder: 'Velg..'
	}

	translations = () => ({
		clearValueText: 'Fjern verdi',
		clearAllText: 'Fjern alle',
		noResultsText: 'Listen er tom',
		searchPromptText: 'Skriv inn for å søke',
		loadingPlaceholder: 'Laster..'
	})

	render() {
		const {
			name,
			label,
			placeholder,
			loadOptions,
			error,
			size,
			hoydeOptions,
			...restProps
		} = this.props

		let restPropsCopy = Object.assign({}, restProps)
		// if (this.props.isMultiple) {
		// 	restPropsCopy.value = {} // <arrayField/> viser ingen verdi i vinduet.
		// }
		return (
			<div className={cn({ error: Boolean(error) }, size, 'skjemaelement dollyselect')}>
				<label htmlFor={name} className="skjemaelement__label">
					{label}{' '}
				</label>
				<div className="dollyselect-input">
					<Select
						async={loadOptions ? true : false}
						loadOptions={loadOptions}
						id={name}
						name={name}
						placeholder={placeholder}
						closeOnSelect={this.props.multi ? false : true}
						clearable
						openOnFocus
						optionHeight={this.findOptionHeight(hoydeOptions)}
						{...this.translations}
						{...restPropsCopy}
					/>
				</div>
				{error && (
					<div role="alert" aria-live="assertive">
						<div className="skjemaelement__feilmelding">{error}</div>
					</div>
				)}
			</div>
		)
	}

	findOptionHeight = hoydeOptions => {
		switch (hoydeOptions) {
			case 'small':
				return 35
			case 'medium':
				return 40
			case 'large':
				return 45
			default:
				return 35
		}
	}
}

export const FormikDollySelect = props => {
	const { field, form, beforeChange, ...restProps } = props
	const singleSelectChangeHandler = selected => {
		form.setFieldValue(field.name, _get(selected, 'value', ''))
	}

	const multiSelectChangeHandler = selected => {
		form.setFieldValue(field.name, selected)
	}

	const onChangeHandler = props.multi ? multiSelectChangeHandler : singleSelectChangeHandler

	const onChange = selected => {
		beforeChange && beforeChange(selected)
		onChangeHandler(selected)
	}

	return (
		<DollySelect
			name={field.name}
			value={field.value}
			onChange={onChange}
			onBlur={() => form.setFieldTouched(field.name, true)}
			error={_get(form.touched, field.name) && _get(form.errors, field.name)}
			{...restProps}
		/>
	)
}
