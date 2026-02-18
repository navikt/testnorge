import Select from 'react-select'

const options = [
	{
		value: 10,
		label: 10,
	},
	{
		value: 20,
		label: 20,
	},
	{
		value: 50,
		label: 50,
	},
]

export default function ItemCountSelect({ value, onChangeHandler }) {
	return (
		<div className="pagination-itemcount">
			Antall elementer i listen
			<Select
				id="item-count"
				className="item-count-select"
				name="item-count"
				openOnFocus
				clearable={false}
				value={options.filter((o) => o.value === Number(value))}
				options={options}
				onChange={onChangeHandler}
			/>
		</div>
	)
}
