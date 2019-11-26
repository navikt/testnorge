import React, { Fragment } from 'react'
import Panel from '~/components/ui/panel/Panel'
import TilgjengeligeMiljoer from '~/components/tilgjengeligeMiljoer/TilgjengeligeMiljoer'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const AttributtPaneler = ({ attributter, valgteAttributter, checkAttributter }) => (
	<div className="attributt-velger_panels">
		{attributter.map(attr => (
			<AttributtKategori
				key={attr.panel.id}
				navn={attr.panel.label}
				kategorier={attr.values}
				infotekst={attr.panel.infotekst}
				tilgjengeligeMiljoeEndepunkt={attr.panel.tilgjengeligeMiljoeEndepunkt}
				checkAttributter={checkAttributter}
			>
				{attr.values.map(({ kategori, values }) => (
					<Fragment key={kategori.id}>
						{!(attr.values.length === 1 && values.length === 1) && <h3>{kategori.label}</h3>}
						<div className="attributt-velger_panelsubcontent">
							{values.map(input => (
								<DollyCheckbox
									key={input.name}
									name={input.name}
									label={input.label}
									size="grow"
									onChange={v => checkAttributter({ [input.name]: !valgteAttributter[input.name] })}
									checked={valgteAttributter[input.name]}
								/>
							))}
						</div>
					</Fragment>
				))}
			</AttributtKategori>
		))}
	</div>
)

const AttributtKategori = ({
	navn,
	kategorier,
	infotekst,
	tilgjengeligeMiljoeEndepunkt,
	checkAttributter,
	children
}) => {
	const allePanelAttrs = kategorier
		.map(x => x.values)
		.flat()
		.map(y => y.name)

	const listToObject = (list, bool) =>
		list.reduce((acc, curr) => {
			acc[curr] = bool
			return acc
		}, {})

	const checkAll = () => checkAttributter(listToObject(allePanelAttrs, true))
	const unCheckAll = () => checkAttributter(listToObject(allePanelAttrs, false))

	const tooltip = () => {
		if (!infotekst && !tilgjengeligeMiljoeEndepunkt) return false
		return (
			<Fragment>
				{infotekst}
				<br />
				<TilgjengeligeMiljoer endepunkt={tilgjengeligeMiljoeEndepunkt} />
			</Fragment>
		)
	}

	return (
		<Panel
			key={navn}
			heading={navn}
			startOpen
			checkAttributeArray={checkAll}
			uncheckAttributeArray={unCheckAll}
			informasjonstekst={tooltip()}
		>
			<fieldset name={navn}>{children}</fieldset>
		</Panel>
	)
}
