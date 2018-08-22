import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Select, { Async } from 'react-select'
import { DollyApi } from '~/service/Api'
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
		const { name, label, placeholder, loadOptions, error, ...restProps } = this.props

		return (
			<div className={cn({ error: Boolean(error) }, 'skjemaelement dollyselect')}>
				<label className="skjemaelement__label">{label} </label>
				<div className="dollyselect-input">
					{loadOptions ? (
						<Async
							name={name}
							loadOptions={loadOptions}
							placeholder={placeholder}
							clearable
							openOnFocus
							{...this.translations}
							{...restProps}
						/>
					) : (
						<Select
							name={name}
							placeholder={placeholder}
							clearable
							openOnFocus
							{...this.translations}
							{...restProps}
						/>
					)}
				</div>
				{error && (
					<div role="alert" aria-live="assertive">
						<div className="skjemaelement__feilmelding">{error}</div>
					</div>
				)}
			</div>
		)
	}
}

export const FormikDollySelect = props => {
	const { field, form, label, item } = props

	let selectSourceProps = {}
	if (item.apiKodeverkId) {
		selectSourceProps.loadOptions = () => {
			return DollyApi.getKodeverkByNavn(item.apiKodeverkId).then(
				DollyApi.Utils.NormalizeKodeverkForDropdown
			)
		}
	} else {
		selectSourceProps.options = item.options
	}

	return (
		<DollySelect
			name={field.name}
			value={field.value}
			label={label}
			onChange={selected => {
				const defaultValue = ''
				form.setFieldValue(field.name, _get(selected, 'value', defaultValue))
			}}
			onBlur={() => form.setFieldTouched(field.name, true)}
			error={_get(form.touched, field.name) && _get(form.errors, field.name)}
			{...selectSourceProps}
		/>
	)
}
